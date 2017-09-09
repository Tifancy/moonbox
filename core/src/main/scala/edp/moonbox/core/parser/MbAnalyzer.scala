package edp.moonbox.core.parser

import edp.moonbox.core._
import org.apache.spark.sql._

/**
  * Created by edp on 9/6/17.
  */
class MbAnalyzer {

	def analyze(cmd: MbCommand): MbCommandExec = cmd match {
		case MountTable(table, options) =>
			MountTableExec(table, options)
		case UnmountTable(table) =>
			UnmountTableExec(table)
		case Select(sql) =>
			SelectExec(sql)
		case CreateViewAsSelect(view, ignore, select) =>
			CreateViewAsSelectExec(view, ignore, analyze(select).asInstanceOf[SelectExec])
		case _ => throw new Exception("unsupported command ")
	}
}
