package com.fablesfantasyrp.plugin.tools

import com.fablesfantasyrp.plugin.utils.SPAWN
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerRespawnEvent

class ToolsListener : Listener {
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	fun onPlayerRespawn(e: PlayerRespawnEvent) {
		e.respawnLocation = SPAWN
	}
}
