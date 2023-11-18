package com.fablesfantasyrp.plugin.item.event

import org.bukkit.entity.ItemFrame
import org.bukkit.entity.Player
import org.bukkit.event.Cancellable
import org.bukkit.event.Event
import org.bukkit.event.HandlerList
import org.bukkit.inventory.ItemStack

class PlayerPutItemInItemFrameEvent(val player: Player,
									val item: ItemStack,
									val itemFrame: ItemFrame,
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
