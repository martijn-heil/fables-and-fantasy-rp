package com.fablesfantasyrp.plugin.whitelist

import com.fablesfantasyrp.plugin.text.miniMessage
import com.fablesfantasyrp.plugin.whitelist.event.WhitelistAddedPlayerEvent
import com.fablesfantasyrp.plugin.whitelist.event.WhitelistRemovedPlayerEvent
import de.myzelyam.api.vanish.VanishAPI
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import org.bukkit.GameMode
import org.bukkit.Server
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority.LOW
import org.bukkit.event.EventPriority.MONITOR
import org.bukkit.event.Listener
import org.bukkit.event.player.*

class WhitelistListener(private val server: Server) : Listener {
	@EventHandler(priority = LOW, ignoreCancelled = true)
	fun onPlayerJoin(e: PlayerJoinEvent) {
		val p = e.player
		if (p.isWhitelisted) return

		server.broadcast(miniMessage.deserialize("<light_purple>(Spectator) <name> joined the game</light_purple>",
			Placeholder.unparsed("name", e.player.name)))

		VanishAPI.hidePlayer(p)
		if (p.gameMode != GameMode.SURVIVAL) p.gameMode = GameMode.SURVIVAL
		if (!p.allowFlight) p.allowFlight = true

		sendWelcomeMessage(p)
	}

	@EventHandler(priority = LOW, ignoreCancelled = true)
	fun onPlayerQuit(e: PlayerQuitEvent) {
		if (!e.player.isWhitelisted) {
			server.broadcast(miniMessage.deserialize("<light_purple>(Spectator) <name> left the game</light_purple>",
				Placeholder.unparsed("name", e.player.name)))
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
		if (!e.player.isWhitelisted) e.isCancelled = true
	}

	@EventHandler(priority = MONITOR, ignoreCancelled = true)
	fun onPlayerWhitelisted(e: WhitelistAddedPlayerEvent) {
		val offlinePlayer = e.offlinePlayer
		offlinePlayer.player?.let { VanishAPI.showPlayer(it) }
		if (offlinePlayer.isOnline) {
			val player = offlinePlayer.player!!
			player.teleport(player.location.world.spawnLocation)
			player.gameMode = GameMode.SURVIVAL
			player.allowFlight = false
			player.kick(Component.text("You have been whitelisted, please relog!"))
		}
	}

	@EventHandler(priority = MONITOR, ignoreCancelled = true)
	fun onPlayerUnWhitelisted(e: WhitelistRemovedPlayerEvent) {
		e.offlinePlayer.player?.kick(Component.text("You have been removed from the whitelist!"))
	}
}
