package edp.moonbox.core.split

import org.apache.spark.sql.catalyst.expressions.AttributeReference
import org.apache.spark.sql.catalyst.plans.logical.LeafNode

/**
  * Created by edp on 17/8/15.
  */
case class PushDownReplaced(alias: String, output: Seq[AttributeReference]) extends LeafNode
