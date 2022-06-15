package com.fablesfantasyrp.plugin.monitoring

import org.apache.logging.log4j.Level
import org.apache.logging.log4j.core.LogEvent
import org.apache.logging.log4j.core.appender.AbstractAppender

class MonitoringAppender() : AbstractAppender("MonitoringAppender", null, null, true, emptyArray()) {
	private fun send(event: LogEvent) {
		logToDiscord("[${event.level}] [${event.loggerName}] ${event.message.formattedMessage}")
	}

	override fun append(event: LogEvent) {
		if (listOf(Level.WARN, Level.ERROR, Level.FATAL).contains(event.level)) {
			send(event)
		}
	}
}
