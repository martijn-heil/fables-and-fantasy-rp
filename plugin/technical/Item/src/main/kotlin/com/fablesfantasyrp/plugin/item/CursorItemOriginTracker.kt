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
