package edp.moonbox.core.split

/**
  * Created by edp on 9/7/17.
  */
object PlatformFactory {
	def newInstance(props: Map[String, String]): Platform = {
		val datasource: String = props.getOrElse("type", throw new Exception)
		datasource.toLowerCase() match {
			case "mysql" | "oracle" | "jdbc" => new JdbcPlatform(props)
			case "es5" | "es" => new EsV5Platform(props)
			case _ => throw new Exception("unknown platform")
		}
	}

	/*def newInstance(relation: BaseRelation, props: Map[String, String]): Platform = {
		relation match {
			case jdbc: JDBCRelation => new JdbcPlatform(props)
			case es: ElasticsearchRelation => new EsV5Platform(props)
		}
	}*/
}
