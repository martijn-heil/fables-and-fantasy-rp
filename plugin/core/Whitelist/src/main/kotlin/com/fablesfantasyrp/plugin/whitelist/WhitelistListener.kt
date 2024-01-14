package com.fablesfantasyrp.plugin.whitelist

import com.fablesfantasyrp.plugin.domain.SPAWN
import com.fablesfantasyrp.plugin.text.sendError
import com.fablesfantasyrp.plugin.utilsoffline.gameMode
import com.fablesfantasyrp.plugin.utilsoffline.location
import de.myzelyam.api.vanish.VanishAPI
import io.papermc.paper.event.server.WhitelistStateUpdateEvent
import net.kyori.adventure.text.Component
import org.bukkit.GameMode
import org.bukkit.Server
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority.*
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.player.*
import org.bukkit.plugin.java.JavaPlugin
import java.util.logging.Level

class WhitelistListener(private val plugin: JavaPlugin) : Listener {
	private val server: Server
		get() = plugin.server

	@EventHandler(priority = HIGHEST, ignoreCancelled = true)
	fun onPlayerJoin(e: PlayerJoinEvent) {
		val p = e.player

		if (p.hasPermission(Permission.SilentJoinQuit)) VanishAPI.hidePlayer(p)
		val message = joinMessage(p)
		if (message == null) {
			e.joinMessage(null)
		} else if (message.recipients != null) {
			e.joinMessage(null)
			message.recipients.forEach { it.sendMessage(message.message) }
		} else {
			e.joinMessage(message.message)
		}

		if (!p.isWhitelisted) {
			if (p.gameMode != GameMode.SURVIVAL) p.gameMode = GameMode.SURVIVAL
			if (!p.allowFlight) p.allowFlight = true

			sendWelcomeMessage(p)

			server.scheduler.scheduleSyncDelayedTask(plugin, { VanishAPI.hidePlayer(p) }, 0)
		}
	}

	@EventHandler(priority = HIGHEST, ignoreCancelled = true)
	fun onPlayerQuit(e: PlayerQuitEvent) {
		val p = e.player
		val message = quitMessage(p)
		if (message == null) {
			e.quitMessage(null)
		} else if (message.recipients != null) {
			e.quitMessage(null)
			message.recipients.forEach { it.sendMessage(message.message) }
		} else {
			e.quitMessage(message.message)
		}
	}

	@EventHandler(priority = LOW, ignoreCancelled = true)
	fun onPlayerInteract(e: PlayerInteractEvent) {
		if (!e.player.isWhitelisted) e.isCancelled = true
	}

	@EventHandler(priority = LOW, ignoreCancelled = true)
	fun onPlayerInteract(e: PlayerInteractEntityEvent) {
		if (!e.player.isWhitelisted) e.isCancelled = true
	}

	@EventHandler(priority = LOW, ignoreCancelled = true)
	fun onPlayerCommandPreprocess(e: PlayerCommandPreprocessEvent) {
		if (!e.player.isWhitelisted) {
			e.isCancelled = true
			e.player.sendError("Spectators cannot execute any command!")
		}
	}

	@EventHandler(priority = LOW, ignoreCancelled = true)
	fun onPlayerAttemptPickupItem(e: PlayerAttemptPickupItemEvent) {
		if (!e.player.isWhitelisted) e.isCancelled = true
	}

	@EventHandler(priority = LOW, ignoreCancelled = true)
	fun onPlayerRecipeDiscover(e: PlayerRecipeDiscoverEvent) {
		if (!e.player.isWhitelisted) e.isCancelled = true
	}

	@EventHandler(priority = LOW, ignoreCancelled = true)
	fun onPlayerStatisticIncrement(e: PlayerStatisticIncrementEvent) {
		if (!e.player.isWhitelisted) e.isCancelled = true
	}

	@EventHandler(priority = LOW, ignoreCancelled = true)
	fun onEntityDamageByPlayer(e: EntityDamageByEntityEvent) {
		val damager = e.damager
		if (damager is Player && !damager.isWhitelisted) e.isCancelled = true
	}

	@EventHandler(priority = LOW, ignoreCancelled = true)
	fun onPlayerDropItem(e: PlayerDropItemEvent) {
		if (!e.player.isWhitelisted) e.isCancelled = true
	}

	@EventHandler(priority = MONITOR, ignoreCancelled = true)
	fun onPlayerWhitelisted(e: WhitelistStateUpdateEvent) {
		val offlinePlayer = e.player

		if (e.status == WhitelistStateUpdateEvent.WhitelistStatus.ADDED) {
			try {
				if (offlinePlayer.hasPlayedBefore()) {
					offlinePlayer.player?.let { VanishAPI.showPlayer(it) }
					offlinePlayer.location = SPAWN
					offlinePlayer.gameMode = GameMode.SURVIVAL
				}
			} catch(ex: Exception) {
				plugin.logger.log(Level.SEVERE, "Error handling WhitelistAddedPlayerEvent", ex)
			} finally {
				offlinePlayer.player?.kick(Component.text("You have been whitelisted, please relog!"))
			}
		} else if (e.status == WhitelistStateUpdateEvent.WhitelistStatus.REMOVED) {
			offlinePlayer.player?.kick(Component.text("You have been removed from the whitelist!"))
		}
	}
}
