package com.fablesfantasyrp.plugin.monitoring

import com.fablesfantasyrp.plugin.utils.enforceDependencies
import com.github.shynixn.mccoroutine.SuspendingJavaPlugin
import org.apache.logging.log4j.LogManager

private val rootLogger
	get() = (LogManager.getRootLogger() as org.apache.logging.log4j.core.Logger)

class FablesMonitoring : SuspendingJavaPlugin() {
	private lateinit var appender: TimeBasedGroupingAppender

	override fun onEnable() {
		enforceDependencies(this)
		instance = this
		saveDefaultConfig()

		appender = TimeBasedGroupingAppender(config.getStringList("ignore_patterns")
				.map { Regex(it) }, MonitoringAppender())
		appender.start()
		server.scheduler.scheduleSyncDelayedTask(this, {
			rootLogger.addAppender(appender)
		}, 20)

		server.scheduler.scheduleSyncRepeatingTask(this, {
			appender.maybeFlush()
		}, 0, 20)
	}

	override fun onDisable() {
		rootLogger.removeAppender(appender)
	}

	companion object {
		lateinit var instance: FablesMonitoring
	}
}
