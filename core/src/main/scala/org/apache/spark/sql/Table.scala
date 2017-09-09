package org.apache.spark.sql

import java.sql.{Connection, DriverManager}
import java.util.{Properties, UUID}

import edp.moonbox.calcite.jdbc.CalciteConnection
import edp.moonbox.common.EdpLogging
import edp.moonbox.core.rdd.CalciteRDD
import edp.moonbox.core.split.{Platform, SQLBuilder}
import org.apache.spark.sql.catalyst.plans.logical.LogicalPlan
import org.apache.spark.sql.types.StructType

/**
  * Created by edp on 17/8/14.
  */

class Table(plan: LogicalPlan, val platform: Platform,  val isResult: Boolean) {
	lazy val sql = sqlBuilder.toSQL
	lazy val name: String = "view_" + UUID.randomUUID().toString.filter(_!='-')
	lazy val sqlBuilder = new SQLBuilder(plan, isResult)
	def schema: StructType = StructType.fromAttributes(sqlBuilder.replacedPlan.output)
	def execute(implicit spark: SparkSession): DataFrame = spark.sql(sql)
}

case class PushDownTable(plan: LogicalPlan, override val platform: Platform, override val isResult: Boolean) extends Table(plan, platform, isResult) with EdpLogging {
	override def execute(implicit spark: SparkSession): DataFrame = {
		//try {
			val rdd = new CalciteRDD(spark.sparkContext,
				platform.getConnection(),
				sql,
				mapRow = rs => Row(CalciteRDD.resultSetToObjectArray(rs):_*)
			)
			spark.createDataFrame(rdd, schema)
		//} catch {
		//	case e: Exception =>
		//		logWarning(s"pushdown sql: $sql \n to platform ${platform.name} error, attempt running in spark")
		//		super.execute
		//}
	}
}
case class ResultTable(plan: LogicalPlan, override val platform: Platform) extends Table(plan, platform, true)

