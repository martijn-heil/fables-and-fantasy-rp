/*
 * Fables and Fantasy RP kotlin plugins.
 * Copyright (C) 2024  Martijn Heil
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.fablesfantasyrp.plugin.morelogging

import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.core.appender.RollingRandomAccessFileAppender
import org.apache.logging.log4j.core.appender.rolling.TimeBasedTriggeringPolicy
import org.apache.logging.log4j.core.layout.PatternLayout
import org.bukkit.plugin.java.JavaPlugin
import java.util.logging.Level
import org.apache.logging.log4j.core.Logger as Log4jLogger

lateinit var MODERATION_LOGGER: java.util.logging.Logger
	private set

internal lateinit var LOG4J_MODERATION_LOGGER: Log4jLogger
	private set

class ModerationLoggerManager(private val plugin: JavaPlugin) {
	fun start() {
		val logsDir = plugin.dataFolder.resolve("logs")
		logsDir.mkdirs()

		val pattern = PatternLayout.newBuilder()
				.withPattern("[%d{HH:mm:ss}] %msg%n")
				.build()

		val appender = RollingRandomAccessFileAppender.Builder()
				.withFileName(logsDir.resolve("latest.log").absolutePath)
				.withFilePattern("${logsDir.absolutePath}/%d{yyyy-MM-dd}-%i.log.gz")
				.setLayout(pattern)
				.withPolicy(TimeBasedTriggeringPolicy.newBuilder().build())
				.setName("FablesModerationFileAppender")
				.build()

		appender.start()

		val logger = LogManager.getLogger("FablesInternalModerationLogger") as Log4jLogger
		logger.isAdditive = false
		logger.addAppender(appender)
		logger.level = org.apache.logging.log4j.Level.ALL
		LOG4J_MODERATION_LOGGER = logger
		MODERATION_LOGGER = java.util.logging.Logger.getLogger("FablesModerationLogger")
		MODERATION_LOGGER.addHandler(ModerationLogHandler())
		MODERATION_LOGGER.level = Level.ALL
		MODERATION_LOGGER.useParentHandlers = false

		plugin.getCommand("modlog")!!.setExecutor(ModerationLogCommand())
	}
}
