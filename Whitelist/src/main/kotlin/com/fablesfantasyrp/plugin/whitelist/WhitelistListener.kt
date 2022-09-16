package com.fablesfantasyrp.plugin.whitelist

import com.fablesfantasyrp.plugin.text.miniMessage
import com.fablesfantasyrp.plugin.text.sendError
import com.fablesfantasyrp.plugin.utils.essentialsSpawn
import com.fablesfantasyrp.plugin.utilsoffline.gameMode
import com.fablesfantasyrp.plugin.utilsoffline.location
import com.fablesfantasyrp.plugin.whitelist.event.WhitelistAddedPlayerEvent
import com.fablesfantasyrp.plugin.whitelist.event.WhitelistRemovedPlayerEvent
import com.github.shynixn.mccoroutine.bukkit.SuspendingJavaPlugin
import de.myzelyam.api.vanish.VanishAPI
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import org.bukkit.GameMode
import org.bukkit.Server
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority.*
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.player.*
import java.util.logging.Level

class WhitelistListener(private val plugin: SuspendingJavaPlugin) : Listener {
	private val server: Server
		get() = plugin.server

	@EventHandler(priority = HIGHEST, ignoreCancelled = true)
	fun onPlayerJoin(e: PlayerJoinEvent) {
		val p = e.player
		if (p.isWhitelisted) return

		e.joinMessage(miniMessage.deserialize("<light_purple>(Spectator) <name> joined the game</light_purple>",
				Placeholder.unparsed("name", e.player.name)))

		if (p.gameMode != GameMode.SURVIVAL) p.gameMode = GameMode.SURVIVAL
		if (!p.allowFlight) p.allowFlight = true

		sendWelcomeMessage(p)

		server.scheduler.scheduleSyncDelayedTask(plugin, { VanishAPI.hidePlayer(p) }, 0)
	}

	@EventHandler(priority = HIGHEST, ignoreCancelled = true)
	fun onPlayerQuit(e: PlayerQuitEvent) {
		if (e.player.isWhitelisted) return
		if (e.quitMessage() == null) return

		e.quitMessage(miniMessage.deserialize("<light_purple>(Spectator) <name> left the game</light_purple>",
				Placeholder.unparsed("name", e.player.name)))
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
	fun onPlayerWhitelisted(e: WhitelistAddedPlayerEvent) {
		val offlinePlayer = e.offlinePlayer

		try {
			if (offlinePlayer.hasPlayedBefore()) {
				offlinePlayer.player?.let { VanishAPI.showPlayer(it) }
				offlinePlayer.location = essentialsSpawn.getSpawn("default")
				offlinePlayer.gameMode = GameMode.SURVIVAL
			}
		} catch(ex: Exception) {
			plugin.logger.log(Level.SEVERE, "Error handling WhitelistAddedPlayerEvent", ex)
		} finally {
			offlinePlayer.player?.kick(Component.text("You have been whitelisted, please relog!"))
		}
	}

	@EventHandler(priority = MONITOR, ignoreCancelled = true)
	fun onPlayerUnWhitelisted(e: WhitelistRemovedPlayerEvent) {
		e.offlinePlayer.player?.kick(Component.text("You have been removed from the whitelist!"))
	}
}
