package com.fablesfantasyrp.plugin.morelogging

import com.fablesfantasyrp.plugin.utils.ToggleableState
import com.fablesfantasyrp.plugin.utils.extensions.bukkit.humanReadable
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority.MONITOR
import org.bukkit.event.Listener
import org.bukkit.event.player.*
import org.bukkit.plugin.Plugin
import java.util.logging.Level
import java.util.logging.Logger

class BukkitListener(private val logger: Logger, private val plugin: Plugin) : Listener {
	@EventHandler(priority = MONITOR, ignoreCancelled = true)
	fun onPlayerGameModeChange(e: PlayerGameModeChangeEvent) {
		val newGameMode = e.newGameMode
		val oldGameMode = e.player.gameMode
		logPlayerStateChange(logger, Level.FINE, e.player, "GAMEMODE", oldGameMode.toString(), newGameMode.toString())
	}

	@EventHandler(priority = MONITOR, ignoreCancelled = true)
	fun onPlayerTeleport(e: PlayerTeleportEvent) {
		if(!e.player.isWhitelisted) return // Prevent excessive console spam from spectators

		val oldPosition = e.from.humanReadable()
		val newPosition = e.to.humanReadable()
		logger.fine("${e.player.name}: TELEPORT $oldPosition -> $newPosition")
	}

	@EventHandler(priority = MONITOR, ignoreCancelled = true)
	fun onPlayerToggleFlight(e: PlayerToggleFlightEvent) {
		if(!e.player.isWhitelisted) return // Prevent excessive console spam from spectators

		val newState = ToggleableState.fromIsActiveBoolean(e.isFlying)
		val oldState = !newState
		logPlayerStateChange(logger, Level.FINEST, e.player, "FLIGHT", oldState.toString(), newState.toString())
	}

	@EventHandler(priority = MONITOR, ignoreCancelled = true)
	fun onPlayerJoin(e: PlayerJoinEvent) {
		logger.fine("[${e.player.location.humanReadable()}] ${e.player.name} joined the game")
	}

	@EventHandler(priority = MONITOR, ignoreCancelled = true)
	fun onPlayerQuit(e: PlayerQuitEvent) {
		logger.fine("[${e.player.location.humanReadable()}] ${e.player.name} left the game. (Reason: ${e.reason})")
	}

	@EventHandler(priority = MONITOR, ignoreCancelled = true)
	fun onPlayerKick(e: PlayerKickEvent) {
		val reason = PlainTextComponentSerializer.plainText().serialize(e.reason())

		logger.fine("[${e.player.location.humanReadable()}] " +
				"${e.player.name} got kicked. (Cause: ${e.cause.name}, Kick reason: \"$reason\")")
	}

	@EventHandler(priority = MONITOR, ignoreCancelled = true)
	fun onPlayerCommand(e: PlayerCommandPreprocessEvent) {

		logger.finer("[${e.player.location.humanReadable()}] " +
				"${e.player.name} issued server command: ${e.message}")
	}
}
