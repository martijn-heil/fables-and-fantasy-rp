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

	private fun fuzzyMatch(event: LogEvent): Boolean {
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

	override fun append(event: LogEvent?) {
		if (event == null) return
		if (event.message == null) return
		val copy = event.toImmutable() ?: return

		val formattedMessage = copy.message.formattedMessage
		if (ignorePatterns.find { it.containsMatchIn(formattedMessage) } != null) return

		maybeFlush(copy.timeMillis)
		grouped.add(copy)
	}
}
