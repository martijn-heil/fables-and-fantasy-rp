package com.fablesfantasyrp.plugin.whitelist.event

import org.bukkit.OfflinePlayer
import org.bukkit.event.Event
import org.bukkit.event.HandlerList

class WhitelistAddedPlayerEvent(val offlinePlayer: OfflinePlayer) : Event() {
	private val handlers = HandlerList()
	override fun getHandlers(): HandlerList = handlers
}

class WhitelistRemovedPlayerEvent(val offlinePlayer: OfflinePlayer) : Event() {
	private val handlers = HandlerList()
	override fun getHandlers(): HandlerList = handlers
}
