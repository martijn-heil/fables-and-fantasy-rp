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

import org.bukkit.World
import org.bukkit.plugin.Plugin
import kotlin.math.roundToLong

private const val SECONDS_IN_DAY = 86400
private const val TICKS_IN_DAY = 24000
private const val TICKS_IN_SECOND: Double = TICKS_IN_DAY.toDouble() / SECONDS_IN_DAY.toDouble()

class WorldTimeSynchronizer(private val plugin: Plugin,
							private val worlds: Collection<World>,
							private val clock: FablesInstantSource) {
	private val server = plugin.server
	private var taskId: Int = 0

	fun start() {
		taskId = server.scheduler.scheduleSyncRepeatingTask(plugin, {
			val epochSecond = clock.instant().epochSecond
			// Offset by -6000 because 0 ticks in Minecraft is actually 6 AM instead of midnight
			val time = (epochSecond.toDouble() * TICKS_IN_SECOND).roundToLong() - 6000
			worlds.forEach { it.time = time }
		}, 0, 1)
		check(taskId != -1)
	}

	fun stop() {
		server.scheduler.cancelTask(taskId)
	}
}
