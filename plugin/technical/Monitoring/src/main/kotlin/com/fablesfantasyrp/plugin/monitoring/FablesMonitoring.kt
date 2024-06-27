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
package com.fablesfantasyrp.plugin.monitoring

import com.fablesfantasyrp.plugin.utils.enforceDependencies
import org.apache.logging.log4j.LogManager
import org.bukkit.plugin.java.JavaPlugin

private val rootLogger: org.apache.logging.log4j.core.Logger?
	get() = (LogManager.getRootLogger() as org.apache.logging.log4j.core.Logger)

class FablesMonitoring : JavaPlugin() {
	private var appender: TimeBasedGroupingAppender? = null

	override fun onEnable() {
		enforceDependencies(this)
		instance = this
		saveDefaultConfig()

		appender = TimeBasedGroupingAppender(config.getStringList("ignore_patterns")
				.map { Regex(it) }, MonitoringAppender())
		appender?.start()
		server.scheduler.scheduleSyncDelayedTask(this, {
			if (appender != null) rootLogger?.addAppender(appender)
		}, 20)

		server.scheduler.scheduleSyncRepeatingTask(this, {
			appender?.maybeFlush()
		}, 0, 20)
	}

	override fun onDisable() {
		rootLogger?.removeAppender(appender)
	}

	companion object {
		lateinit var instance: FablesMonitoring
	}
}
