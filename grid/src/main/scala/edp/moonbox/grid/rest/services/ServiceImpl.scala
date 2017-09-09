/*-
 * <<
 * Moonbox
 * ==
 * Copyright (C) 2016 - 2017 EDP
 * ==
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * >>
 */

package edp.moonbox.grid.rest.services

import edp.moonbox.core.rest.entities._
import edp.moonbox.grid.{JobInfo, JobType}
import edp.moonbox.grid.message._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.reflect.ClassTag

/**
  * Created by edp on 17/7/5.
  */
trait ServiceImpl extends ServiceInterface {

	val computer: ComputeService
	val dataGetter: DataFetchService


	private def entityToMessage(entity: Entity): MbMessage = {
		entity match {
			case SqlQuery(sessionId, sqlList) => {
				val jobType = if (sessionId.isDefined) {
					JobType.ADHOC
				} else {
					JobType.BATCH
				}
				val job = JobInfo(jobType, sessionId, "", sqlList)
				JobSubmit(job)
			}
			case SqlProgress(sessionId, jobId) => JobProgress(jobId, sessionId)
			case SqlOpenSession => OpenSession
			case SqlCancel(sessionId, jobId) => JobCancel(jobId, sessionId)
			case SqlCloseSession(sessionId) => CloseSession(sessionId)
			case res: SqlResult => FetchData(res.jobId, res.offset, res.size)
		}
	}

	private def messageToEntity[T: ClassTag](message: => Future[MbMessage]): Future[T] = {
		message.map(messageToEntity).mapTo[T]
	}

	private def messageToEntity(message: MbMessage): Entity = {
		message match {
			case JobAccepted(jobId, _) => {
				Response(jobId, "job accepted")
			}
			case JobProgressResponse(jobState) => {
				val message =
					s""" update time: ${jobState.updateTime}
					   |${jobState.jobStatus}
					 """.stripMargin
				Response(jobState.jobId, message)
			}
			case FetchDataResponse(jobId, data, message) => {
				val msg = if (data.isDefined) data.mkString(",") else message
				Response(jobId, msg)
			}
		}
	}

	override def sqlQuery(query: SqlQuery): Future[Response] = messageToEntity {
		computer.service(entityToMessage(query))
	}

	override def sqlCancel(cancel: SqlCancel): Future[Response] = messageToEntity {
		computer.service(entityToMessage(cancel))
	}

	override def sqlOpenSession(open: SqlOpenSession.type): Future[Response] = messageToEntity {
		computer.service(entityToMessage(open))
	}

	override def sqlProgress(progress: SqlProgress): Future[Response] = messageToEntity {
		computer.service(entityToMessage(progress))
	}

	override def sqlCloseSession(close: SqlCloseSession): Future[Response] = messageToEntity {
		computer.service(entityToMessage(close))
	}

	override def sqlResult(result: SqlResult): Future[Response] = messageToEntity {
		dataGetter.service(entityToMessage(result))
	}

}
