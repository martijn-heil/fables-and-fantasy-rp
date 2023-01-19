package com.fablesfantasyrp.plugin.profile.event

import org.bukkit.entity.Player
import org.bukkit.event.Cancellable
import org.bukkit.event.Event
import org.bukkit.event.HandlerList


class PlayerForceProfileSelectionEvent(val player: Player) : Event(), Cancellable {
	override fun getHandlers(): HandlerList = Companion.handlers

	private var isCancelled = false
	override fun isCancelled() = this.isCancelled
	override fun setCancelled(cancel: Boolean) { this.isCancelled = cancel }

	companion object {
		private val handlers = HandlerList()

		@JvmStatic
		fun getHandlerList() = handlers
	}
}
