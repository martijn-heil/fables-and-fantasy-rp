package com.fablesfantasyrp.plugin.monitoring

import org.apache.logging.log4j.Level
import org.apache.logging.log4j.core.Appender
import org.apache.logging.log4j.core.LogEvent
import org.apache.logging.log4j.core.appender.AbstractAppender
import org.apache.logging.log4j.core.impl.Log4jLogEvent
import org.apache.logging.log4j.message.SimpleMessage
import java.util.*

private fun mergeLogEvents(events: Collection<LogEvent>, level: Level): LogEvent {
	check(events.isNotEmpty())
	val finalMessage = events
			.joinToString("\n") {
				listOfNotNull(it.message.formattedMessage, it.message.throwable?.stackTraceToString())
						.joinToString("\n")
			}

	return Log4jLogEvent.Builder()
			.setLevel(level)
			.setLoggerName(events.first().loggerName)
			.setTimeMillis(events.first().timeMillis)
			.setMessage(SimpleMessage(finalMessage))
			.build()
}

private fun getHighestLevel(events: Collection<LogEvent>): Level {
	val levels = events.map { it.level }
	return when {
		levels.contains(Level.FATAL) -> Level.FATAL
		levels.contains(Level.ERROR) -> Level.ERROR
		levels.contains(Level.WARN) -> Level.WARN
		else -> Level.INFO
	}
}

class TimeBasedGroupingAppender(private val ignorePatterns: Collection<Regex>, private val child: Appender) :
		AbstractAppender("MonitoringAppender", null, null, true, emptyArray()) {
	private val grouped = LinkedList<LogEvent>()

	private fun fuzzyMatch(event: LogEvent?): Boolean {
		if (checkNull(event)) return false
		if (event == null) return false
		return event.message.formattedMessage.lowercase().contains("error")
	}

	fun flush() {
		val fuzzy = grouped.toList().find { fuzzyMatch(it) } != null
		if (fuzzy) {
			child.append(mergeLogEvents(grouped, Level.ERROR))
		} else {
			val filtered = grouped.filter { listOf(Level.FATAL, Level.ERROR, Level.WARN).contains(it.level) }
			if (filtered.isNotEmpty()) child.append(mergeLogEvents(filtered, getHighestLevel(filtered)))
		}
		grouped.clear()
	}

	fun maybeFlush(time: Long = System.currentTimeMillis()) {
		val first = grouped.firstOrNull()
		if (first != null && (time - first.timeMillis > 50)) this.flush()
	}

	private fun checkNull(event: LogEvent?): Boolean {
		return event == null ||
			// confirmed to never be null in documentation.
			event.contextMap == null ||
			event.contextData == null ||
			event.contextStack == null ||

			// not confirmed to never be null, but unlikely to be.
			event.loggerFqcn == null ||
			event.level == null ||
			event.message == null ||
			event.instant == null

			// confirmed to be able to be nullable.
			// event.loggerName
			// event.marker
			// event.source
			// event.threadName
			// event.thrown
	}
	override fun append(event: LogEvent?) {
		if (checkNull(event)) return
		val copy = event?.toImmutable() ?: return

		val formattedMessage = copy.message.formattedMessage
		if (ignorePatterns.find { it.containsMatchIn(formattedMessage) } != null) return

		maybeFlush(copy.timeMillis)
		grouped.add(copy)
	}
}
