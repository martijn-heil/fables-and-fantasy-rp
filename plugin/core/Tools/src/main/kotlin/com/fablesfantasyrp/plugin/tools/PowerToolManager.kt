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

import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.plugin.Plugin
import java.util.*

class PowerToolManager(private val plugin: Plugin) {
	private val server = plugin.server
	private val powerTools: MutableMap<Player, MutableMap<Material, String>> = HashMap()

	fun start() {
		server.pluginManager.registerEvents(PowerToolListener(), plugin)
	}

	fun stop() {
		powerTools.clear()
	}

	fun setPowerTool(player: Player, material: Material, command: String?) {
		if (command != null) {
			powerTools
					.computeIfAbsent(player) { EnumMap(org.bukkit.Material::class.java) }
					.put(material, command)
		} else {
			powerTools.remove(player)
		}
	}

	fun getPowerTool(player: Player, material: Material): String? {
		return powerTools[player]?.get(material)
	}

	inner class PowerToolListener : Listener {
		@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = false)
		fun onPlayerInteract(e: PlayerInteractEvent) {
			if (e.hand != EquipmentSlot.HAND) return
			val material = e.player.inventory.itemInMainHand.type
			val command = getPowerTool(e.player, material) ?: return
			e.isCancelled = true
			e.player.performCommand(command)
		}

		@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
		fun onPlayerQuit(e: PlayerQuitEvent) {
			powerTools.remove(e.player)
		}
	}
}
