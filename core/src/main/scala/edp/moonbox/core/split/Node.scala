package edp.moonbox.core.split

import org.apache.spark.sql.catalyst.plans.logical.LogicalPlan

/**
  * Created by edp on 17/8/14.
  */
case class Node(plan: LogicalPlan, platform: Platform , dep: Seq[Dependency])
