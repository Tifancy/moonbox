package edp.moonbox.grid.worker

import akka.actor.{Actor, ActorRef, Props}
import akka.actor.Actor.Receive
import akka.cluster.{Cluster, Member}
import akka.cluster.ClusterEvent.MemberUp
import edp.moonbox.common.EdpLogging
import edp.moonbox.grid.{JobState, Running}
import edp.moonbox.grid.master.MbMaster
import edp.moonbox.grid.message.{ActiveMasterHasChanged, MbMessage, RunJob, WorkerRegister}

import scala.collection.mutable._
import scala.concurrent.duration._

/**
  * Created by edp on 9/9/17.
  */
class WorkerBackend(endpoint: ActorRef, param: WorkerParam) extends Actor with EdpLogging {
	import context.dispatcher

	private val runningJobs = new HashMap[String, JobState]()
	private val cluster = Cluster(context.system)

	override def preStart(): Unit = {
		cluster.subscribe(self, classOf[MemberUp])
	}
	override def postStop(): Unit = {
		cluster.unsubscribe(self)
	}

	override def receive: Receive = {
		case r@RunJob(_) => {
			handleRunJob(r)
		}

		case ActiveMasterHasChanged => {
			registerDelay()
		}

		case MemberUp(member) => {
			register(member)
		}

	}

	private def handleRunJob(message: RunJob): Unit = {
		val jobState = message.jobState
		runningJobs.put(jobState.jobId, jobState.copy(jobStatus = Running(0)))
		val jobRunner = context.actorOf(Props(classOf[JobRunner], param.conf))
		jobRunner forward message
	}

	private def register(member: Member) = {
		if (member.hasRole(MbMaster.ROLE))
			endpoint ! WorkerRegister(WorkerState(self, runningJobs.size))
	}

	private def registerDelay(): Unit = {
		context.system.scheduler.scheduleOnce(FiniteDuration(10, SECONDS),
			endpoint, WorkerRegister(WorkerState(self, runningJobs.size)))
	}


}

object WorkerBackend {
	val NAME = "workerbackend"
}
