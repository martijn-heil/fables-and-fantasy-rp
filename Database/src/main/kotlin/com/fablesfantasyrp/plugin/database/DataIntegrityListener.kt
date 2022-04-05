package com.fablesfantasyrp.plugin.database

import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority.MONITOR
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent

class DataIntegrityListener : Listener {
	@EventHandler(priority = MONITOR)
	fun onPlayerJoin(e: PlayerJoinEvent) {
		ensurePresenceInDatabase(e.player)
	}
}
