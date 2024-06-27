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
package com.fablesfantasyrp.plugin.playeresp

import com.fablesfantasyrp.plugin.glowing.GlowingManager
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority.NORMAL
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.plugin.Plugin
import java.util.*

class PlayerEspManager(private val plugin: Plugin, private val glowingManager: GlowingManager) {
	private val server = plugin.server
	private var isStopped = false
	private val esp = HashSet<UUID>()

	fun start() {
		server.pluginManager.registerEvents(PlayerEspListener(), plugin)
	}

	fun stop() {
		esp.mapNotNull { server.getPlayer(it) }.forEach { glowDown(it) }
		esp.clear()
		isStopped = true
	}

	fun setEsp(player: Player, value: Boolean) {
		if (value) {
			esp.add(player.uniqueId)
			glowUp(player)
		} else {
			esp.remove(player.uniqueId)
			glowDown(player)
		}
	}

	fun hasEsp(player: Player): Boolean {
		return esp.contains(player.uniqueId)
	}

	private fun glowUp(viewer: Player) {
		for (glowing in server.onlinePlayers.asSequence().minus(viewer)) {
			glowingManager.setIsGlowingFor(glowing, viewer, true)
		}
	}

	private fun glowDown(viewer: Player) {
		for (glowing in server.onlinePlayers.asSequence().minus(viewer)) {
			glowingManager.setIsGlowingFor(glowing, viewer, false)
		}
	}

	private inner class PlayerEspListener : Listener {
		@EventHandler(priority = NORMAL, ignoreCancelled = true)
		fun onPlayerJoin(e: PlayerJoinEvent) {
			if (isStopped) return
			server.scheduler.scheduleSyncDelayedTask(plugin) {
				for (viewer in esp.mapNotNull { server.getPlayer(it) }) {
					if (viewer == e.player) continue
					glowingManager.setIsGlowingFor(e.player, viewer, true)
				}
			}
		}

		@EventHandler(priority = NORMAL, ignoreCancelled = true)
		fun onPlayerQuit(e: PlayerQuitEvent) {
			if (isStopped) return
			setEsp(e.player, false)
		}
	}
}
