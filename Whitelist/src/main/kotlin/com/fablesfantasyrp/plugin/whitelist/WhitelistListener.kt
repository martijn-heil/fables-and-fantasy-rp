package com.fablesfantasyrp.plugin.whitelist

import com.fablesfantasyrp.plugin.text.miniMessage
import com.fablesfantasyrp.plugin.text.sendError
import com.fablesfantasyrp.plugin.utils.essentialsSpawn
import com.fablesfantasyrp.plugin.utilsoffline.gameMode
import com.fablesfantasyrp.plugin.utilsoffline.location
import com.fablesfantasyrp.plugin.whitelist.event.WhitelistAddedPlayerEvent
import com.fablesfantasyrp.plugin.whitelist.event.WhitelistRemovedPlayerEvent
import com.github.shynixn.mccoroutine.SuspendingJavaPlugin
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

class WhitelistListener(private val plugin: SuspendingJavaPlugin) : Listener {
	private val server: Server
		get() = plugin.server

	@EventHandler(priority = LOW, ignoreCancelled = true)
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

	@EventHandler(priority = LOW, ignoreCancelled = true)
	fun onPlayerQuit(e: PlayerQuitEvent) {
		if (!e.player.isWhitelisted) {
			e.quitMessage(miniMessage.deserialize("<light_purple>(Spectator) <name> left the game</light_purple>",
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
		if (!e.player.isWhitelisted) {
			e.isCancelled = true
			e.player.sendError("Spectators cannot execute any command!")
		}
	}

	@EventHandler(priority = MONITOR, ignoreCancelled = true)
	fun onPlayerWhitelisted(e: WhitelistAddedPlayerEvent) {
		val offlinePlayer = e.offlinePlayer

		if (offlinePlayer.hasPlayedBefore()) {
			offlinePlayer.player?.let { VanishAPI.showPlayer(it) }
			offlinePlayer.location = essentialsSpawn.getSpawn("default")
			offlinePlayer.gameMode = GameMode.SURVIVAL
		}

		offlinePlayer.player?.kick(Component.text("You have been whitelisted, please relog!"))
	}

	@EventHandler(priority = MONITOR, ignoreCancelled = true)
	fun onPlayerUnWhitelisted(e: WhitelistRemovedPlayerEvent) {
		e.offlinePlayer.player?.kick(Component.text("You have been removed from the whitelist!"))
	}
}
