package edp.moonbox.grid

import edp.moonbox.grid.JobType.JobType

/**
  * Created by edp on 9/8/17.
  */
object JobType extends Enumeration {
	type JobType = Value
	val BATCH, ADHOC, STREAMING, UNKNOWN = Value
}

sealed trait JobStatus {
	def message: String
}

case class Running(progress: Int) extends JobStatus {
	override def message: String = {
		s"job is running, progress is $progress/100"
	}
}
case class Waiting(submitTime: Long) extends JobStatus {
	def duration: Long = System.currentTimeMillis() - submitTime

	override def message: String = {
		s"job is waiting for running, has been wait ${duration/1000} sec"
	}
}
case class Complete(resultSize: Long) extends JobStatus {
	override def message: String = {
		s"job is completed, result size is $resultSize"
	}
}
case class Failed(message: String) extends JobStatus
case class NotFound(message: String = "not found") extends JobStatus

case class JobInfo(jobType: JobType,
                   sessionId: Option[String],
                   jobId: String,
                   sqlList: Seq[String],
                   jobDesc: Option[String] = None)

case class JobState(jobType: JobType,
                    sessionId: Option[String],
                    jobId: String,
                    sqlList: Seq[String],
                    jobDesc: Option[String] = None,
                    jobStatus: JobStatus,
                    submitTime: Long,
                    updateTime: Long)
