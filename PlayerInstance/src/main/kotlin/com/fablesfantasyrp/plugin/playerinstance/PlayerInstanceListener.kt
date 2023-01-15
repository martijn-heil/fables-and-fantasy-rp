package com.fablesfantasyrp.plugin.playerinstance

import com.fablesfantasyrp.plugin.playerinstance.data.entity.EntityPlayerInstanceRepository
import com.fablesfantasyrp.plugin.playerinstance.data.entity.PlayerInstance
import com.fablesfantasyrp.plugin.playerinstance.event.PostPlayerSwitchPlayerInstanceEvent
import com.fablesfantasyrp.plugin.text.sendError
import com.fablesfantasyrp.plugin.utils.SPAWN
import com.fablesfantasyrp.plugin.utils.isVanished
import com.github.shynixn.mccoroutine.bukkit.launch
import de.myzelyam.api.vanish.VanishAPI
import kotlinx.coroutines.CancellationException
import org.bukkit.entity.Player
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

	private suspend fun forcePlayerInstanceSelection(player: Player, ownedInstances: Collection<PlayerInstance>) {
		playerInstanceManager.stopTracking(player) // Just to be safe
		player.teleport(SPAWN)
		player.inventory.clear()
		player.enderChest.clear()
		require(playerInstanceManager.getCurrentForPlayer(player) == null)

		do {
			val selector = server.servicesManager.getRegistration(PlayerInstanceSelectionPrompter::class.java)!!.provider
			try {
				if (!player.isOnline || server.isStopping) return
				val wasVanished = player.isVanished
				if (!wasVanished) VanishAPI.hidePlayer(player)
				val newInstance = selector.promptSelect(player, ownedInstances)
				playerInstanceManager.setCurrentForPlayer(player, newInstance)
				player.sendMessage("$SYSPREFIX You are now player instance #${newInstance.id}")
				if (!wasVanished) VanishAPI.showPlayer(player)
			} catch (ex: PlayerInstanceOccupiedException) {
				player.sendError("This player instance is currently occupied.")
			} catch (_: CancellationException) {}
		} while (playerInstanceManager.getCurrentForPlayer(player) == null)
		require(playerInstanceManager.getCurrentForPlayer(player) != null)
	}

	@EventHandler(priority = MONITOR, ignoreCancelled = true)
	fun onPlayerJoin(e: PlayerJoinEvent) {
		if (!e.player.isWhitelisted) return // TODO leaky abstraction from whitelist plugin

		val ownedInstances = instances.forOwner(e.player).filter { it.isActive }
		if (ownedInstances.isEmpty()) return

		server.scheduler.scheduleSyncDelayedTask(plugin, {
			val player = server.getPlayer(e.player.uniqueId) ?: return@scheduleSyncDelayedTask
			plugin.launch { forcePlayerInstanceSelection(player, ownedInstances) }
		}, 1)
	}

	@EventHandler(priority = MONITOR, ignoreCancelled = true)
	fun onPlayerInstanceSwitch(e: PostPlayerSwitchPlayerInstanceEvent) {
		if (e.new == null && e.old != null) {
			val ownedInstances = instances.forOwner(e.player).filter { it.isActive }
			plugin.launch { forcePlayerInstanceSelection(e.player, ownedInstances) }
		}
	}

	@EventHandler(priority = MONITOR, ignoreCancelled = true)
	fun onPlayerQuit(e: PlayerQuitEvent) {
		playerInstanceManager.stopTracking(e.player)
	}
}
