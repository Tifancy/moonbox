package edp.moonbox.core.rdd

import java.sql.ResultSet

import edp.moonbox.calcite.jdbc.CalciteConnection
import org.apache.spark.annotation.DeveloperApi
import org.apache.spark.rdd.RDD
import org.apache.spark.{Partition, SparkContext, TaskContext}

import scala.reflect.ClassTag

/**
  * Created by root on 11/2/16.
  */
object CalciteRDD {

  def resultSetToObjectArray(rs: ResultSet): Array[Object] = {
    Array.tabulate[Object](rs.getMetaData.getColumnCount)(i => rs.getObject(i + 1))
  }

}

class CalciteRDD[T: ClassTag](sc: SparkContext,
                              getConnection: () => CalciteConnection,
                              sql: String,
                              lowerBound: Long = 0,
                              upperBound: Long = 0,
                              numPartitions: Int = 1,
                              mapRow: (ResultSet) => T)
                            extends RDD[T](sc, Nil) {

  @DeveloperApi
  override def compute(split: Partition, context: TaskContext): Iterator[T] = new NextIterator[T] {

    context.addTaskCompletionListener( context => closeIfNeeded())

    val partition = split.asInstanceOf[CalciteRDDPartition]
    val conn = getConnection()
    val stmt = conn.createStatement()
    val recordsNumPerPartition = (upperBound - lowerBound + 1) / numPartitions
    //2 3 4 5 6 7 8 9 10 11
    private val replacedSql: String = sql.replaceFirst("\\?", (partition.index * recordsNumPerPartition + lowerBound).toString)
    val replacedSql1 = replacedSql.replaceFirst("\\?",((partition.index + 1) * recordsNumPerPartition + lowerBound - 1).toString)
    val resultSet = stmt.executeQuery(sql)

    override protected def close(): Unit = {
      try {
        if (null != resultSet) {
          resultSet.close()
        }
      } catch {
        case e: Exception => logWarning("Exception closing resultset", e)
      }
      try {
        if (null != stmt) {
          stmt.close()
        }
      } catch {
        case e: Exception => logWarning("Exception closing statement", e)
      }
      try {
        if (null != conn) {
          conn.close()
        }
        logInfo("closed connection")
      } catch {
        case e: Exception => logWarning("Exception closing connection", e)
      }
    }


    override protected def getNext(): T = {
      if (resultSet.next()) {
        mapRow(resultSet)
      } else {
        finished = true
        null.asInstanceOf[T]
      }
    }
  }


  override protected def getPartitions: Array[Partition] = {
    (0 until numPartitions).map { i =>
      new CalciteRDDPartition(i)
    }.toArray
  }

}

class CalciteRDDPartition(idx: Int) extends Partition {
  override def index: Int = idx
}
