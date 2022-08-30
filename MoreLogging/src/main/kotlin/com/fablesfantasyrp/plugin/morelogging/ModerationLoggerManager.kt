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
		logger.addAppender(appender)
		logger.level = org.apache.logging.log4j.Level.ALL
		LOG4J_MODERATION_LOGGER = logger
		MODERATION_LOGGER = java.util.logging.Logger.getLogger("FablesModerationLogger")
		MODERATION_LOGGER.addHandler(ModerationLogHandler())
		MODERATION_LOGGER.level = Level.ALL

		plugin.getCommand("modlog")!!.setExecutor(ModerationLogCommand())
	}
}
