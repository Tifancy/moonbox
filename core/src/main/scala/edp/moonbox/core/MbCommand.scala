package edp.moonbox.core

/**
  * Created by edp on 9/6/17.
  */
sealed trait MbCommand

case class MountTable(name: String, options: Map[String, String]) extends MbCommand
case class UnmountTable(name: String) extends MbCommand
case class Select(sql: String) extends MbCommand
case class CreateViewAsSelect(name: String, ignore: Boolean, select: Select) extends MbCommand

