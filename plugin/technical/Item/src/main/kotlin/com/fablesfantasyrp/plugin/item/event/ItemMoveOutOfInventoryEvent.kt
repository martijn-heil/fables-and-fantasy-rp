package com.fablesfantasyrp.plugin.item.event

import org.bukkit.event.Cancellable
import org.bukkit.event.Event
import org.bukkit.event.HandlerList
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack

class ItemMoveOutOfInventoryEvent(val item: ItemStack,
								  val inventory: Inventory,
								  private var isCancelled: Boolean) : Event(), Cancellable {
	override fun getHandlers(): HandlerList = Companion.handlers

	companion object {
		private val handlers = HandlerList()

		@JvmStatic
		fun getHandlerList() = handlers
	}

	override fun isCancelled(): Boolean = this.isCancelled
	override fun setCancelled(cancel: Boolean) { this.isCancelled = cancel }
}
