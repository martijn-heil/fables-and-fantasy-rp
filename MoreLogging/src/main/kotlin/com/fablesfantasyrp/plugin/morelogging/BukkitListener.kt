package com.fablesfantasyrp.plugin.morelogging

import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority.MONITOR
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerGameModeChangeEvent
import org.bukkit.event.player.PlayerTeleportEvent
import org.bukkit.event.player.PlayerToggleFlightEvent
import org.bukkit.plugin.Plugin
import java.util.logging.Logger

class BukkitListener(private val logger: Logger, private val plugin: Plugin) : Listener {
	@EventHandler(priority = MONITOR, ignoreCancelled = true)
	fun onPlayerGameModeChange(e: PlayerGameModeChangeEvent) {
		val newGameMode = e.newGameMode
		val oldGameMode = e.player.gameMode
		logPlayerStateChange(logger, e.player, "GAMEMODE", oldGameMode.toString(), newGameMode.toString())
	}

	@EventHandler(priority = MONITOR, ignoreCancelled = true)
	fun onPlayerTeleport(e: PlayerTeleportEvent) {
		if(!e.player.isWhitelisted) return // Prevent excessive console spam from spectators

		val oldPosition = e.from.humanReadable()
		val newPosition = e.to.humanReadable()
		logger.info("${e.player.name}: TELEPORT $oldPosition -> $newPosition")
	}

	@EventHandler(priority = MONITOR, ignoreCancelled = true)
	fun onPlayerToggleFlight(e: PlayerToggleFlightEvent) {
		if(!e.player.isWhitelisted) return // Prevent excessive console spam from spectators

		val newState = ToggleableState.fromIsActiveBoolean(e.isFlying)
		val oldState = !newState
		logPlayerStateChange(logger, e.player, "FLIGHT", oldState.toString(), newState.toString())
	}
}
