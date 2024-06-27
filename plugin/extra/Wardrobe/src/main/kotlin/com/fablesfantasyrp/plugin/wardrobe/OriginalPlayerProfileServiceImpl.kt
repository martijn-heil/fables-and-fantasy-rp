/*
 * Fables and Fantasy RP kotlin plugins.
 * Copyright (C) 2024  Martijn Heil
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
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
