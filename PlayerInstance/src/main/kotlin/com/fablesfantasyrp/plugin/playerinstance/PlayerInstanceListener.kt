package com.fablesfantasyrp.plugin.playerinstance

import com.fablesfantasyrp.plugin.form.promptGui
import com.fablesfantasyrp.plugin.playerinstance.gui.PlayerInstanceSelectionGui
import com.fablesfantasyrp.plugin.utils.isVanished
import com.github.shynixn.mccoroutine.bukkit.launch
import de.myzelyam.api.vanish.VanishAPI
import kotlinx.coroutines.CancellationException
import org.bukkit.Server
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority.MONITOR
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent

class PlayerInstanceListener(private val server: Server) : Listener {
	@EventHandler(priority = MONITOR, ignoreCancelled = true)
	fun onPlayerJoin(e: PlayerJoinEvent) {
		PLUGIN.launch {
			do {
				try {
					if (!e.player.isOnline || server.isStopping) return@launch
					val wasVanished = e.player.isVanished
					if (!wasVanished) VanishAPI.hidePlayer(e.player)
					e.player.currentPlayerInstance = e.player.promptGui(PlayerInstanceSelectionGui(PLUGIN,
							playerInstances.forOwner(e.player).asSequence().filter { it.isActive }))
					if (!wasVanished) VanishAPI.showPlayer(e.player)
				} catch (_: CancellationException) {}
			} while (e.player.currentPlayerInstance == null)
		}
	}

	@EventHandler(priority = MONITOR, ignoreCancelled = true)
	fun onPlayerQuit(e: PlayerQuitEvent) {
		e.player.currentPlayerInstance = null
	}
}
