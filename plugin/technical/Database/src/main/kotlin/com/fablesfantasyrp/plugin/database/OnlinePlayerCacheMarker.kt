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
package com.fablesfantasyrp.plugin.database

import net.kyori.adventure.text.Component
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.plugin.Plugin

class OnlinePlayerCacheMarker<T>(private val plugin: Plugin, private val marker: CacheMarker<T>, private val forPlayer: (p: Player) -> T) {
	fun start(): OnlinePlayerCacheMarker<T> {
		plugin.server.pluginManager.registerEvents(object : Listener {
			@EventHandler(priority = EventPriority.LOWEST)
			fun onPlayerJoin(e: PlayerJoinEvent) {
				try {
					marker.markStrong(forPlayer(e.player))
				} catch(ex: Exception) {
					ex.printStackTrace()
					e.player.kick(Component.text("An internal server error occurred! Please contact server staff."))
				}
			}

			@EventHandler(priority = EventPriority.MONITOR)
			fun onPlayerQuit(e: PlayerQuitEvent) {
				marker.markWeak(forPlayer(e.player))
			}
		}, plugin)

		return this
	}
}
