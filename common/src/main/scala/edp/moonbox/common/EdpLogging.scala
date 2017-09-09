package edp.moonbox.common

import org.slf4j.{Logger, LoggerFactory}

/**
  * Created by edp on 9/6/17.
  */
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
