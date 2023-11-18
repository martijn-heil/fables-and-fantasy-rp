package com.fablesfantasyrp.plugin.tools

import org.bukkit.Location
import org.bukkit.Server
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority.MONITOR
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.event.player.PlayerTeleportEvent
import org.bukkit.plugin.Plugin
import java.util.*

class BackManager(private val plugin: Plugin) {
	private val server: Server = plugin.server

	private val previousLocations = HashMap<UUID, Location>()

	init {
		server.pluginManager.registerEvents(BackManagerListener(), plugin)
	}

	fun getPreviousLocation(player: Player): Location? = previousLocations[player.uniqueId]

	private inner class BackManagerListener : Listener {
		@EventHandler(priority = MONITOR, ignoreCancelled = true)
		fun onPlayerQuit(e: PlayerQuitEvent) {
			previousLocations.remove(e.player.uniqueId)
		}

		@EventHandler(priority = MONITOR, ignoreCancelled = true)
		fun onPlayerTeleport(e: PlayerTeleportEvent) {
			previousLocations[e.player.uniqueId] = e.from
		}
	}
}
