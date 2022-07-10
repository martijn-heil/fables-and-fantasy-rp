package com.fablesfantasyrp.plugin.database

import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.plugin.Plugin

class OnlinePlayerCacheMarker<T>(private val plugin: Plugin, private val marker: CacheMarker<T>, private val forPlayer: (p: Player) -> T) {
	fun start(): OnlinePlayerCacheMarker<T> {
		plugin.server.pluginManager.registerEvents(object : Listener {
			@EventHandler(priority = EventPriority.LOWEST)
			fun onPlayerJoin(e: PlayerJoinEvent) {
				marker.markStrong(forPlayer(e.player))
			}

			@EventHandler(priority = EventPriority.MONITOR)
			fun onPlayerQuit(e: PlayerQuitEvent) {
				marker.markWeak(forPlayer(e.player))
			}
		}, plugin)

		return this
	}
}
