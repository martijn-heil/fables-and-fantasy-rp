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
package com.fablesfantasyrp.plugin.weights

import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority.NORMAL
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityPickupItemEvent
import org.bukkit.event.player.PlayerPickupArrowEvent
import org.bukkit.plugin.Plugin
import java.util.*

class ItemPickupManager(private val plugin: Plugin) {
	private val server = plugin.server
	private val pickupDisabled = HashSet<UUID>()

	fun start() {
		server.pluginManager.registerEvents(ItemPickupManagerListener(), plugin)
	}

	fun setPickupDisabled(player: Player, value: Boolean)
		= if (value) pickupDisabled.add(player.uniqueId) else pickupDisabled.remove(player.uniqueId)

	fun hasPickupDisabled(player: Player) = pickupDisabled.contains(player.uniqueId)

	private inner class ItemPickupManagerListener : Listener {
		@EventHandler(priority = NORMAL, ignoreCancelled = true)
		fun onPlayerPickupItem(e: EntityPickupItemEvent) {
			val player = e.entity as? Player ?: return
			if (hasPickupDisabled(player)) {
				e.isCancelled = true
			}
		}

		@EventHandler(priority = NORMAL, ignoreCancelled = true)
		fun onPlayerPickupArrow(e: PlayerPickupArrowEvent) {
			if (hasPickupDisabled(e.player)) {
				e.isCancelled = true
			}
		}
	}
}
