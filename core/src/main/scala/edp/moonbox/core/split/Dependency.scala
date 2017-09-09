package edp.moonbox.core.split

/**
  * Created by edp on 17/8/11.
  */
abstract class Dependency extends Serializable {
	def node: Node
}

case class SamePlatformDependency(_node: Node) extends Dependency {
	override def node: Node = _node
}

case class CrossPlatformDependency(_node: Node) extends Dependency {
	override def node: Node = _node
}
