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
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.entity.EntityPickupItemEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.plugin.Plugin

class NoEnchanting(private val plugin: Plugin) : Mechanic {
	private val server = plugin.server

	override fun init() {
		server.pluginManager.registerEvents(NoEnchantingListener(), plugin)
	}

	inner class NoEnchantingListener : Listener {
		@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
		fun onPlayerInteract(e: PlayerInteractEvent) {
			if (e.action != Action.RIGHT_CLICK_BLOCK) return
			val clickedBlock = e.clickedBlock ?: return
			if (clickedBlock.type == Material.ENCHANTING_TABLE) {
				e.isCancelled = true
			}
		}

		@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
		fun onPlayerItemPickup(e: EntityPickupItemEvent) {
			if (e.item.itemStack.enchantments.isNotEmpty()) {
				e.item.itemStack.enchantments.keys.forEach { e.item.itemStack.removeEnchantment(it) }
			}
		}
	}
}
