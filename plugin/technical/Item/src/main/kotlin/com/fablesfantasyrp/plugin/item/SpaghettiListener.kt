package com.fablesfantasyrp.plugin.item

import com.fablesfantasyrp.plugin.item.event.ItemMoveOutOfInventoryEvent
import com.fablesfantasyrp.plugin.item.event.PlayerPutItemInItemFrameEvent
import org.bukkit.Material
import org.bukkit.Server
import org.bukkit.entity.EntityType
import org.bukkit.entity.ItemFrame
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryAction.*
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.player.PlayerInteractEntityEvent
import org.bukkit.inventory.EquipmentSlot


class SpaghettiListener(private val server: Server,
						private val cursorItemOriginService: CursorItemOriginService) : Listener {

	@EventHandler(priority = EventPriority.HIGHEST)
	fun onPlayerInteractEntity(e: PlayerInteractEntityEvent) {
		// If player tries to put a soulbound item in an item frame
		if (e.rightClicked.type == EntityType.ITEM_FRAME && e.hand == EquipmentSlot.HAND) {
			val item = e.player.inventory.itemInMainHand
			if (item.type != Material.AIR) {
				if(!PlayerPutItemInItemFrameEvent(e.player, item, e.rightClicked as ItemFrame, e.isCancelled).callEvent()) {
					e.isCancelled = true
				}

				if(!ItemMoveOutOfInventoryEvent(item, e.player.inventory, e.isCancelled).callEvent()) {
					e.isCancelled = true
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	fun onInventoryClick(e: InventoryClickEvent) {
		val action = e.action
		val item = e.currentItem
		val cursorItem = e.cursor
		val clickedInventory = e.clickedInventory
		val player = e.whoClicked as? Player ?: return

		// Drop item from slot by pressing Q
		if (clickedInventory != null && (action == DROP_ALL_SLOT || action == DROP_ONE_SLOT)) {
			if (item != null && item.type != Material.AIR) {
				if (!ItemMoveOutOfInventoryEvent(item, clickedInventory, e.isCancelled).callEvent()) {
					e.isCancelled = true
				}
			}
		}

		// Drop item from cursor
		if (cursorItem != null && (action == DROP_ALL_CURSOR || action == DROP_ONE_CURSOR)) {
			val origin = cursorItemOriginService.getOriginatingInventory(cursorItem)
			if (origin != null && !ItemMoveOutOfInventoryEvent(cursorItem, origin, e.isCancelled).callEvent()) {
				e.isCancelled = true
			}
		}

		// Pick up item, then click in another inventory to put it down there
		if (cursorItem != null && (action == PLACE_ALL || action == PLACE_ONE || action == PLACE_SOME)) {
			val origin = cursorItemOriginService.getOriginatingInventory(cursorItem)
			if (origin != null && !ItemMoveOutOfInventoryEvent(cursorItem, origin, e.isCancelled).callEvent()) {
				e.isCancelled = true
			}
		}

		// Shift right click item to move it to another inventory
		if (item != null && clickedInventory != null && action == MOVE_TO_OTHER_INVENTORY) {
			if (!ItemMoveOutOfInventoryEvent(item, clickedInventory, e.isCancelled).callEvent()) {
				e.isCancelled = true
			}
		}

		// Hover over item in top inventory, then press number button to swap with hotbar slot
		if (action == HOTBAR_SWAP && clickedInventory != null && clickedInventory != player.inventory) {
			if (item != null && !ItemMoveOutOfInventoryEvent(item, clickedInventory, e.isCancelled).callEvent()) {
				e.isCancelled = true
			}

			val hotbarItem = when(e.click) {
				ClickType.SWAP_OFFHAND -> player.inventory.itemInOffHand
				ClickType.NUMBER_KEY -> player.inventory.getItem(e.hotbarButton)
				else -> null
			}

			if (hotbarItem != null && hotbarItem.type != Material.AIR) {
				if (!ItemMoveOutOfInventoryEvent(hotbarItem, player.inventory, e.isCancelled).callEvent()) {
					e.isCancelled = true
				}
			}
		}
	}
}
