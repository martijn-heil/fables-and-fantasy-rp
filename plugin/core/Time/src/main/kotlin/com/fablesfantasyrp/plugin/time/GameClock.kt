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
package com.fablesfantasyrp.plugin.time

import com.fablesfantasyrp.plugin.time.event.NewDayEvent
import com.fablesfantasyrp.plugin.time.javatime.FablesLocalDate
import com.fablesfantasyrp.plugin.time.javatime.chrono.FablesChronology
import org.bukkit.plugin.Plugin
import java.time.Instant

class GameClock(private val plugin: Plugin,
				private var startTime: Instant,
				private val speed: Int) : FablesInstantSource {
	private var isStopped = false
	private val server = plugin.server
	private var taskId: Int = 0
	var milliseconds = startTime.toEpochMilli()

	fun start() {
		taskId = server.scheduler.scheduleSyncRepeatingTask(plugin, {
			val oldEpochDay = milliseconds / FablesChronology.MILLISECONDS_IN_DAY
			if (!isStopped) milliseconds += 50*speed
			val newEpochDay = milliseconds / FablesChronology.MILLISECONDS_IN_DAY
			if (oldEpochDay != newEpochDay) {
				server.pluginManager.callEvent(NewDayEvent(
					FablesLocalDate.ofEpochDay(oldEpochDay),
					FablesLocalDate.ofEpochDay(newEpochDay)
				))
			}
		}, 0, 1)
		check(taskId != -1)
	}

	fun stop() {
		isStopped = true
		server.scheduler.cancelTask(taskId)
	}

	override fun instant(): Instant = Instant.ofEpochMilli(milliseconds)
}
