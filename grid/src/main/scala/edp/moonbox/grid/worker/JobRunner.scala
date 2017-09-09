package edp.moonbox.grid.worker

import akka.actor.{Actor, ActorRef, PoisonPill}
import akka.actor.Actor.Receive
import edp.moonbox.common.{EdpLogging, RedisDFHandler}
import edp.moonbox.core.MbConf
import edp.moonbox.grid.{Complete, Failed, JobState, JobType}
import edp.moonbox.grid.message.{CancelJob, JobStateReport, RunJob}
import org.apache.spark.sql.MbSession

import scala.concurrent.Future
import scala.util.{Failure, Success}

/**
  * Created by edp on 9/9/17.
  */
class JobRunner(conf: MbConf) extends Actor with EdpLogging {
	import context.dispatcher

	val mb = MbSession(conf)
	override def receive: Receive = {
		case RunJob(jobState) =>
			run(jobState).onComplete {
				case Success(size) =>
					successCallback(jobState, size, sender())
				case Failure(e) =>
					failureCallback(jobState, e, sender())
			}
		case CancelJob(jobState) => {
			mb.cancelJobGroup(jobState.jobId)
		}
	}

	private def run(jobState: JobState): Future[Long] = {
		Future[Long] {
			mb.setJobGroup(jobState.jobId, jobState.jobDesc.getOrElse(""))
			val dataframe = mb.sql(jobState.sqlList)
			mb.manipulate(jobState.jobId, dataframe,
				Map("servers" -> conf.cacheServers),
				new RedisDFHandler).asInstanceOf[Long]
		}
	}

	private def successCallback(jobState: JobState, size: Long, sender: ActorRef): Unit = {
		sender ! JobStateReport(jobState.copy(
			jobStatus = Complete(size),
			updateTime = System.currentTimeMillis())
		)
		if (jobState.jobType == JobType.BATCH) self ! PoisonPill
	}

	private def failureCallback(jobState: JobState, e: Throwable, sender: ActorRef): Unit = {
		sender ! JobStateReport(jobState.copy(
			jobStatus = Failed(e.getMessage),
			updateTime = System.currentTimeMillis())
		)
		if (jobState.jobType == JobType.BATCH) self ! PoisonPill
	}
}
