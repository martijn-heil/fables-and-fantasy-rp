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
package com.fablesfantasyrp.plugin.alternatemechanics.mechanic

import com.fablesfantasyrp.plugin.alternatemechanics.Mechanic
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.PlayerLeashEntityEvent
import org.bukkit.event.vehicle.VehicleEnterEvent
import org.bukkit.plugin.Plugin

class NoLeashingWhileInVehicle(private val plugin: Plugin) : Mechanic {
	private val server = plugin.server

	override fun init() {
		server.pluginManager.registerEvents(NoLeashingWhileInVehicleListener(), plugin)
	}

	inner class NoLeashingWhileInVehicleListener : Listener {
		@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
		fun onPlayerLeashEntity(e: PlayerLeashEntityEvent) {
			if (e.player.isInsideVehicle) {
				e.player.sendRichMessage("<red>You can't leash entities while in a vehicle.")
				e.isCancelled = true
			}
		}

		@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
		fun onPlayerEnterVehicle(e: VehicleEnterEvent) {
			val player = e.entered as? Player ?: return
			val location = player.location
			location.getNearbyLivingEntities(30.0)
				.filter { it.isLeashed && it.leashHolder == player }
				.forEach {
					it.setLeashHolder(null)
					location.world.playSound(location, Sound.ENTITY_LEASH_KNOT_BREAK, 1f, 1f)
				}
		}
	}
}
