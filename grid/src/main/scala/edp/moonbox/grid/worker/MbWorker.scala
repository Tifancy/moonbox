package edp.moonbox.grid.worker

import akka.actor.{ActorRef, ActorSystem, Props}
import akka.cluster.singleton.{ClusterSingletonProxy, ClusterSingletonProxySettings}
import com.typesafe.config.ConfigFactory
import edp.moonbox.common.EdpLogging
import edp.moonbox.core.MbConf
import edp.moonbox.grid.master.MbMaster
import org.apache.spark.sql.MbSession

import scala.concurrent.duration._

/**
  * Created by edp on 9/8/17.
  */
class MbWorker(param: WorkerParam) extends EdpLogging {
	import MbWorker._

	val akkaSystem = ActorSystem(MbMaster.CLUSTER_NAME, ConfigFactory.parseMap(param.akka))

	def startUp(): Unit = {
		param.conf.systemAddJars.foreach(MbSession.addJar)
		val endpoint = startEndpoint()
		startBackend(endpoint)
	}

	private def startBackend(endpoint: ActorRef): ActorRef = {
		akkaSystem.actorOf(Props(classOf[WorkerBackend], endpoint, param), WorkerBackend.NAME)
	}

	private def startEndpoint(): ActorRef = {
		val singletonProps = ClusterSingletonProxy.props(
			settings = ClusterSingletonProxySettings(akkaSystem)
				.withRole(MbMaster.ROLE).withBufferSize(5000)
				.withSingletonIdentificationInterval(FiniteDuration(5, SECONDS)),
			singletonManagerPath = MbMaster.SINGLETON_MANAGER_PATH)

		val endpoint = akkaSystem.actorOf(singletonProps, MbMaster.SINGLETON_PROXY_NAME)
		endpoint
	}
}

object MbWorker {
	val ROLE = "worker"
	val PATH = s"/user/$ROLE"

	def main(args: Array[String]) {
		val conf = new MbConf(true)
		val param: WorkerParam = new WorkerParam(args, conf)
		new MbWorker(param).startUp()

	}
}
