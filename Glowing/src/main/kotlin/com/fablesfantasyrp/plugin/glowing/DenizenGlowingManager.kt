package com.fablesfantasyrp.plugin.glowing

import com.denizenscript.denizen.objects.PlayerTag
import com.fablesfantasyrp.plugin.denizeninterop.ex
import org.bukkit.Server
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority.NORMAL
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.plugin.Plugin

class DenizenGlowingManager(private val plugin: Plugin) : GlowingManager {
	private val server = plugin.server
	private val glowingMap = HashMap<Player, MutableSet<Player>>()

	fun start() {
		server.pluginManager.registerEvents(GlowingListener(server, this), plugin)
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
			glowingMap.getOrPut(viewing) { HashSet() }.add(glowing)
		} else {
			this.unglowFor(glowing, viewing)
			glowingMap.getOrPut(viewing) { HashSet() }.remove(glowing)
		}
	}

	private fun glowFor(glowing: Player, viewing: Player) {
		ex(mapOf(
				Pair("player", PlayerTag.mirrorBukkitPlayer(viewing)),
				Pair("target", PlayerTag.mirrorBukkitPlayer(glowing))
		),
				"adjust <queue> linked_player:<[player]>",
				"glow <[target]>")
	}

	private fun unglowFor(glowing: Player, viewing: Player) {
		ex(mapOf(
				Pair("player", PlayerTag.mirrorBukkitPlayer(viewing)),
				Pair("target", PlayerTag.mirrorBukkitPlayer(glowing))
		),
				"adjust <queue> linked_player:<[player]>",
				"glow <[target]> false")
	}

	override fun isGlowingFor(glowing: Player, viewing: Player): Boolean {
		return glowingMap.getOrPut(viewing) { HashSet() }.contains(glowing)
	}

	private class GlowingListener(private val server: Server,
						  private val glowingManager: DenizenGlowingManager) : Listener {
		@EventHandler(priority = NORMAL, ignoreCancelled = true)
		fun onPlayerJoin(e: PlayerJoinEvent) {
			server.onlinePlayers.forEach { glowingManager.update(e.player, it) }
		}
	}
}
