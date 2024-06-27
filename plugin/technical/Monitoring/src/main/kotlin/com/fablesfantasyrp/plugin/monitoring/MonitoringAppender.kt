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
