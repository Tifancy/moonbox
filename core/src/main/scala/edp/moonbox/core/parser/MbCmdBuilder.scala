package edp.moonbox.core.parser

import edp.moonbox.core._
import edp.moonbox.core.parser.CMDParser._

import scala.collection.JavaConverters._
/**
  * Created by edp on 9/6/17.
  */
class MbCmdBuilder extends CMDBaseVisitor[AnyRef] {

	override def visitMountTable(ctx: MountTableContext): MbCommand = {
		val (table, database) = visitTableIdentifier(ctx.tableIdentifier())
		if (database.isDefined) throw new Exception("database don't allowed in mount table ")
		val options = visitTablePropertyList(ctx.tablePropertyList())
		MountTable(table , options)
	}

	override def visitUnmountTable(ctx: UnmountTableContext): MbCommand = {
		val (table, database) = visitTableIdentifier(ctx.tableIdentifier())
		if (database.isDefined) throw new Exception("database don't allowed in unmount table ")
		UnmountTable(table)
	}

	override def visitQuery(ctx: QueryContext): Select = {
		Select(ctx.start.getInputStream.toString.substring(ctx.start.getStartIndex))
	}

	override def visitCmdDefault(ctx: CmdDefaultContext): MbCommand = {
		visitQuery(ctx.query())
	}

	override def visitCreateViewAsSelect(ctx: CreateViewAsSelectContext): MbCommand = {
		val (view, database) = visitTableIdentifier(ctx.tableIdentifier())
		if (database.isDefined) throw new Exception("database don't allowed in mount table ")
		CreateViewAsSelect(view, ctx.EXISTS() != null, visitQuery(ctx.query()))
	}

	override def visitSingleCmd(ctx: SingleCmdContext): MbCommand = {
		visit(ctx.cmd()).asInstanceOf[MbCommand]
	}

	override def visitTableIdentifier(ctx: TableIdentifierContext): (String, Option[String]) = {
		val table: String = ctx.table.getText
		val database: Option[String] = Option(ctx.db).map(_.getText)
		(table, database)
	}

	override def visitTablePropertyKey(ctx: TablePropertyKeyContext): String = {
		if (ctx.STRING() != null) ctx.STRING().getText
		else ctx.getText
	}

	override def visitTablePropertyList(ctx: TablePropertyListContext): Map[String, String] = {
		ctx.tableProperty().asScala.map { property =>
			val key = visitTablePropertyKey(property.key)
			val value = Option(property.value).map(_.getText).orNull
			key -> value.replaceAll("^\'|^\"|\"$|\'$", "")
		}.toMap
	}
}
