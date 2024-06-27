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
package com.fablesfantasyrp.plugin.item

import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryAction
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.Plugin
import java.util.*

class CursorItemOriginTracker(private val plugin: Plugin) : CursorItemOriginService {
	private val server = plugin.server

	private val itemOrigin = WeakHashMap<ItemStack, Inventory>()

	fun init() {
		server.pluginManager.registerEvents(CursorItemOriginTrackerListener(), plugin)
	}

	override fun getOriginatingInventory(itemStack: ItemStack): Inventory? = itemOrigin[itemStack]

	inner class CursorItemOriginTrackerListener : Listener {
		private val pickupActions = hashSetOf(
			InventoryAction.PICKUP_ALL,
			InventoryAction.PICKUP_HALF,
			InventoryAction.PICKUP_ONE,
			InventoryAction.PICKUP_SOME)

		@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = false)
		fun onItemPickup(e: InventoryClickEvent) {
			val inventory = e.clickedInventory ?: return
			val slot = e.slot
			val item = inventory.getItem(slot) ?: return

			// TODO take InventoryAction.COLLECT_TO_CURSOR and InventoryAction.SWAP_TO_CURSOR into account

			if (pickupActions.contains(e.action)) {
				itemOrigin[item] = inventory
			}
		}

		@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = false)
		fun onItemDrop(e: InventoryClickEvent) {
			val action = e.action
			if (action != InventoryAction.DROP_ONE_CURSOR && action != InventoryAction.DROP_ALL_CURSOR) return
			val item = e.cursor ?: return
			itemOrigin.remove(item)
		}
	}
}
