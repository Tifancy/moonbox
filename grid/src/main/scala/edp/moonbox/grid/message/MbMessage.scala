package edp.moonbox.grid.message

import edp.moonbox.grid.worker.WorkerState
import edp.moonbox.grid.{JobInfo, JobState}


/**
  * Created by edp on 9/8/17.
  */
sealed trait MbMessage

case object ActiveMasterHasChanged extends MbMessage

case class WorkerRegister(state: WorkerState) extends MbMessage

case class JobSubmit(job: JobInfo) extends MbMessage
case class JobAccepted(jobId: String, sessionId: Option[String] = None) extends MbMessage

case class JobProgress(jobId: String, sessionId: Option[String] = None) extends MbMessage
case class JobProgressResponse(jobState: JobState) extends MbMessage

case class JobCancel(jobId: String, sessionId: Option[String] = None) extends MbMessage
case class JobCancelResponse(message: String) extends MbMessage

case object OpenSession extends MbMessage
case class SessionOpened(sessionId: String) extends MbMessage
case class SessionOpenFailed(message: String) extends MbMessage

case class CloseSession(sessionId: String) extends MbMessage
case class SessionClosed(sessionId: String) extends MbMessage
case class SessionCloseFailed(sessionId: String, message: String) extends MbMessage

case class WorkerStateReport(workerState: WorkerState) extends MbMessage
case class JobStateReport(jobState: JobState) extends MbMessage

case class RunJob(jobState: JobState) extends MbMessage
case class CancelJob(jobState: JobState) extends MbMessage

case class FetchData(jobId: String, offset: Long, size: Long) extends MbMessage
case class FetchDataResponse(jobId: String, data: Option[Seq[String]], message: String) extends MbMessage
