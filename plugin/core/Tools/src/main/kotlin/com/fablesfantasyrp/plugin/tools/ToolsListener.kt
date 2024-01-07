package com.fablesfantasyrp.plugin.tools

import com.fablesfantasyrp.plugin.utils.SPAWN
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerRespawnEvent

class ToolsListener : Listener {
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	fun onPlayerRespawn(e: PlayerRespawnEvent) {
		e.respawnLocation = SPAWN
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	fun onPlayerJoin(e: PlayerJoinEvent) {
		e.player.resetPlayerWeather()
		e.player.resetPlayerTime()
	}
}
