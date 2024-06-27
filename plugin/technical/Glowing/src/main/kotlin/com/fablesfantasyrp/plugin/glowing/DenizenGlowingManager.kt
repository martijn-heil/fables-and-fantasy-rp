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
package com.fablesfantasyrp.plugin.glowing

import com.denizenscript.denizen.objects.PlayerTag
import com.fablesfantasyrp.plugin.denizeninterop.ex
import me.neznamy.tab.api.TabAPI
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority.NORMAL
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.plugin.Plugin
import java.util.*

class DenizenGlowingManager(private val plugin: Plugin, private val tapi: TabAPI) : GlowingManager {
	private val server = plugin.server
	private var isStopped = false
	override val defaultGlowColor = ChatColor.WHITE
	private val glowingMap = HashMap<UUID, MutableSet<UUID>>()
	private val colors = HashMap<UUID, ChatColor>()

	fun start() {
		server.pluginManager.registerEvents(GlowingListener(), plugin)
		for (glowing in server.onlinePlayers) {
			for (viewer in server.onlinePlayers) {
				this.update(glowing, viewer)
			}
		}
	}

	fun stop() {
		for (glowing in server.onlinePlayers) {
			for (viewer in server.onlinePlayers) {
				this.setIsGlowingFor(glowing, viewer, false)
			}
		}
		isStopped = true
	}

	private fun update(glowing: Player, viewer: Player) {
		if (this.isGlowingFor(glowing, viewer)) {
			this.glowFor(glowing, viewer)
		} else {
			this.unglowFor(glowing, viewer)
		}
	}

	override fun setIsGlowingFor(glowing: Player, viewing: Player, value: Boolean) {
		if (value) {
			this.glowFor(glowing, viewing)
			glowingMap.getOrPut(viewing.uniqueId) { HashSet() }.add(glowing.uniqueId)
		} else {
			this.unglowFor(glowing, viewing)
			glowingMap.getOrPut(viewing.uniqueId) { HashSet() }.remove(glowing.uniqueId)
		}
	}

	override fun setGlowColor(glowing: Player, value: ChatColor?) {
		if (value != null) {
			colors[glowing.uniqueId] = value
		} else {
			colors.remove(glowing.uniqueId)
		}
		updatePlayerTeam(glowing)
	}

	override fun getGlowColor(glowing: Player): ChatColor? {
		return colors[glowing.uniqueId]
	}

	override fun isGlowingFor(glowing: Player, viewing: Player): Boolean {
		return glowingMap.getOrPut(viewing.uniqueId) { HashSet() }.contains(glowing.uniqueId)
	}

	private fun glowFor(glowing: Player, viewing: Player) {
		ex(mapOf(
			Pair("player", PlayerTag.mirrorBukkitPlayer(viewing)),
			Pair("target", PlayerTag.mirrorBukkitPlayer(glowing))
		), "glow <[target]> true for:<[player]>")
		server.scheduler.scheduleSyncDelayedTask(plugin) { reloadPlayer(glowing, viewing) }
	}

	private fun unglowFor(glowing: Player, viewing: Player) {
		ex(mapOf(
			Pair("player", PlayerTag.mirrorBukkitPlayer(viewing)),
			Pair("target", PlayerTag.mirrorBukkitPlayer(glowing))
		), "glow <[target]> false for:<[player]>")
		server.scheduler.scheduleSyncDelayedTask(plugin) { reloadPlayer(glowing, viewing) }
	}

	private fun reloadPlayer(glowing: Player, viewing: Player) {
		if (!glowing.isOnline || !viewing.isOnline) return
		if (glowing == viewing) return
		if (!viewing.canSee(glowing)) return

		viewing.hidePlayer(plugin, glowing)
		viewing.showPlayer(plugin, glowing)
	}

	private fun reset(glowing: Player) {
		server.onlinePlayers.forEach { setIsGlowingFor(glowing, it, false) }
		colors.remove(glowing.uniqueId)
	}

	private fun updatePlayerTeam(player: Player) {
		val glowColor = getGlowColor(player) ?: defaultGlowColor
		val tabPlayer = tapi.getPlayer(player.uniqueId) ?: return
		val nameTagManager = tapi.nameTagManager ?: throw IllegalStateException()
		nameTagManager.setPrefix(tabPlayer, glowColor.toString())
	}

	private inner class GlowingListener : Listener {
		@EventHandler(priority = NORMAL, ignoreCancelled = true)
		fun onPlayerJoin(e: PlayerJoinEvent) {
			if (isStopped) return
			server.onlinePlayers.forEach { update(e.player, it) }
			server.scheduler.scheduleSyncDelayedTask(plugin) { updatePlayerTeam(e.player) }
		}

		@EventHandler(priority = NORMAL, ignoreCancelled = true)
		fun onPlayerQuit(e: PlayerQuitEvent) {
			if (isStopped) return
			reset(e.player)
		}
	}
}
