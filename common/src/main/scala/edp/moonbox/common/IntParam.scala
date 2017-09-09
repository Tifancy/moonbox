package edp.moonbox.common

/**
  * Created by edp on 9/8/17.
  */
object IntParam {
	def unapply(arg: String): Option[Int] = {
		try {
			Some(arg.toInt)
		} catch {
			case e: NumberFormatException => None
		}
	}
}
