package com.fablesfantasyrp.plugin.playerinstance

import com.fablesfantasyrp.plugin.form.promptGui
import com.fablesfantasyrp.plugin.playerinstance.data.entity.EntityPlayerInstanceRepository
import com.fablesfantasyrp.plugin.playerinstance.gui.PlayerInstanceSelectionGui
import com.fablesfantasyrp.plugin.text.sendError
import com.fablesfantasyrp.plugin.utils.isVanished
import com.github.shynixn.mccoroutine.bukkit.launch
import de.myzelyam.api.vanish.VanishAPI
import kotlinx.coroutines.CancellationException
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority.MONITOR
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.plugin.java.JavaPlugin

class PlayerInstanceListener(private val plugin: JavaPlugin,
							 private val instances: EntityPlayerInstanceRepository,
							 private val playerInstanceManager: PlayerInstanceManager) : Listener {
	private val server get() = plugin.server

	@EventHandler(priority = MONITOR, ignoreCancelled = true)
	fun onPlayerJoin(e: PlayerJoinEvent) {
		e.player.inventory.clear()
		e.player.enderChest.clear()

		if (!e.player.isWhitelisted) return // TODO leaky abstraction from whitelist plugin

		val ownedInstances = instances.forOwner(e.player).filter { it.isActive }
		if (ownedInstances.isEmpty()) return

		plugin.launch {
			do {
				try {
					if (!e.player.isOnline || server.isStopping) return@launch
					val wasVanished = e.player.isVanished
					if (!wasVanished) VanishAPI.hidePlayer(e.player)
					playerInstanceManager.setCurrentForPlayer(e.player, e.player.promptGui(PlayerInstanceSelectionGui(plugin, ownedInstances.asSequence())))
					if (!wasVanished) VanishAPI.showPlayer(e.player)
				} catch (ex: PlayerInstanceOccupiedException) {
					e.player.sendError("This player instance is currently occupied.")
				} catch (_: CancellationException) {}
			} while (playerInstanceManager.getCurrentForPlayer(e.player) == null)
		}
	}

	@EventHandler(priority = MONITOR, ignoreCancelled = true)
	fun onPlayerQuit(e: PlayerQuitEvent) {
		playerInstanceManager.stopTracking(e.player)
	}
}
