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

package edp.moonbox.common

import org.slf4j.{Logger, LoggerFactory}


trait EdpLogging {
	@transient private var logger : Logger = null

	protected def logName = {
		this.getClass.getName.stripSuffix("$")
	}

	protected def log: Logger = {
		if (logger == null) {
			logger = LoggerFactory.getLogger(logName)
		}
		logger
	}

	protected def logInfo(msg: => String) {
		if (log.isInfoEnabled) log.info(msg)
	}

	protected def logDebug(msg: => String) {
		if (log.isDebugEnabled) log.debug(msg)
	}

	protected def logTrace(msg: => String) {
		if (log.isTraceEnabled) log.trace(msg)
	}

	protected def logWarning(msg: => String) {
		if (log.isWarnEnabled) log.warn(msg)
	}

	protected def logError(msg: => String) {
		if (log.isErrorEnabled) log.error(msg)
	}

	protected def logInfo(msg: => String, throwable: Throwable) {
		if (log.isInfoEnabled) log.info(msg, throwable)
	}

	protected def logDebug(msg: => String, throwable: Throwable) {
		if (log.isDebugEnabled) log.debug(msg, throwable)
	}

	protected def logTrace(msg: => String, throwable: Throwable) {
		if (log.isTraceEnabled) log.trace(msg, throwable)
	}

	protected def logWarning(msg: => String, throwable: Throwable) {
		if (log.isWarnEnabled) log.warn(msg, throwable)
	}

	protected def logError(msg: => String, throwable: Throwable) {
		if (log.isErrorEnabled) log.error(msg, throwable)
	}

	protected def isTraceEnabled: Boolean = {
		log.isTraceEnabled
	}
}
