package com.fablesfantasyrp.plugin.morelogging

import org.apache.logging.log4j.Level
import org.bukkit.Bukkit
import java.util.logging.Handler
import java.util.logging.LogRecord
import java.util.logging.Level as JulLevel

/**
 * All log events get logged to file.
 * Log events with level equal to or greater than FINER can be received by online moderators.
 * Log events with level equal to or greater than FINE get logged to console.
 * Log events with level equal to or greater than WARN get sent to all online moderators and logged to discord.
 * Log events with level equal to or greater than SEVERE get sent to discord with a ping to the mod team.
 *
 * Flight modes -> FINEST
 * All chat and commands -> FINER
 * Significant state switches -> FINE
 * Lockpicking success etc -> WARN
 */
class ModerationLogHandler : Handler() {
	override fun publish(record: LogRecord?) {
		if (record == null) return

		val level = record.level
		val log4jLevel = when (level) {
			java.util.logging.Level.ALL -> Level.ALL
			java.util.logging.Level.INFO -> Level.INFO
			java.util.logging.Level.WARNING -> Level.WARN
			java.util.logging.Level.SEVERE -> Level.FATAL
			java.util.logging.Level.CONFIG -> Level.INFO
			java.util.logging.Level.FINE -> Level.DEBUG
			java.util.logging.Level.FINER -> Level.TRACE
			java.util.logging.Level.FINEST -> Level.TRACE
			java.util.logging.Level.OFF -> Level.OFF
			else -> Level.INFO
		}

		LOG4J_MODERATION_LOGGER.log(log4jLevel, record.message)

		if (level.intValue() >= JulLevel.FINE.intValue()) {
			val newLevel = if (record.level.intValue() < JulLevel.INFO.intValue()) JulLevel.INFO else level
			Bukkit.getLogger().log(newLevel, record.message)
		}
	}

	override fun flush() {
		// do nothing
	}

	override fun close() {
		// do nothing
	}
}
