package com.fablesfantasyrp.plugin.database

import net.kyori.adventure.text.Component
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
				try {
					marker.markStrong(forPlayer(e.player))
				} catch(ex: Exception) {
					ex.printStackTrace()
					e.player.kick(Component.text("An internal server error occurred! Please contact server staff."))
				}
			}

			@EventHandler(priority = EventPriority.MONITOR)
			fun onPlayerQuit(e: PlayerQuitEvent) {
				marker.markWeak(forPlayer(e.player))
			}
		}, plugin)

		return this
	}
}
