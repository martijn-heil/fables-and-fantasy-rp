package com.fablesfantasyrp.plugin.whitelist.event

import org.bukkit.OfflinePlayer
import org.bukkit.event.Event
import org.bukkit.event.HandlerList

class WhitelistAddedPlayerEvent(val offlinePlayer: OfflinePlayer) : Event() {
	override fun getHandlers(): HandlerList = Companion.handlers

	companion object {
		private val handlers = HandlerList()

		@JvmStatic
		fun getHandlerList() = handlers
	}
}

class WhitelistRemovedPlayerEvent(val offlinePlayer: OfflinePlayer) : Event() {
	override fun getHandlers(): HandlerList = Companion.handlers

	companion object {
		private val handlers = HandlerList()

		@JvmStatic
		fun getHandlerList() = handlers
	}
}
