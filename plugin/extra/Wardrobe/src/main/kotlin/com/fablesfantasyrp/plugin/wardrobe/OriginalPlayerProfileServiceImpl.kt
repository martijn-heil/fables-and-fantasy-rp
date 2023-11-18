package com.fablesfantasyrp.plugin.wardrobe

import com.destroystokyo.paper.profile.PlayerProfile
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.plugin.Plugin
import java.util.*

class OriginalPlayerProfileServiceImpl(private val plugin: Plugin) : OriginalPlayerProfileService {
	private val playerProfiles = HashMap<UUID, PlayerProfile>()
	private val server = plugin.server

	override fun getOriginalPlayerProfile(uuid: UUID): PlayerProfile? = playerProfiles[uuid]

	fun init() {
		server.pluginManager.registerEvents(OriginalProfileTrackerListener(), plugin)
	}

	inner class OriginalProfileTrackerListener : Listener {
		@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
		fun onPlayerJoinEvent(e: PlayerJoinEvent) {
			playerProfiles[e.player.uniqueId] = e.player.playerProfile
		}
	}
}
