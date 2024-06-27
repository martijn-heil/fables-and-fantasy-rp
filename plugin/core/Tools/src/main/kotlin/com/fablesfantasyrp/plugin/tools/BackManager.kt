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
package com.fablesfantasyrp.plugin.tools

import org.bukkit.Location
import org.bukkit.Server
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority.MONITOR
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.event.player.PlayerTeleportEvent
import org.bukkit.plugin.Plugin
import java.util.*

class BackManager(private val plugin: Plugin) {
	private val server: Server = plugin.server

	private val previousLocations = HashMap<UUID, Location>()

	init {
		server.pluginManager.registerEvents(BackManagerListener(), plugin)
	}

	fun getPreviousLocation(player: Player): Location? = previousLocations[player.uniqueId]

	private inner class BackManagerListener : Listener {
		@EventHandler(priority = MONITOR, ignoreCancelled = true)
		fun onPlayerQuit(e: PlayerQuitEvent) {
			previousLocations.remove(e.player.uniqueId)
		}

		@EventHandler(priority = MONITOR, ignoreCancelled = true)
		fun onPlayerTeleport(e: PlayerTeleportEvent) {
			previousLocations[e.player.uniqueId] = e.from
		}
	}
}
