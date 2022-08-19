package com.fablesfantasyrp.plugin.morelogging

import com.fablesfantasyrp.plugin.utils.ToggleableState
import de.myzelyam.api.vanish.PlayerVanishStateChangeEvent
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority.MONITOR
import org.bukkit.event.Listener
import org.bukkit.plugin.Plugin
import java.util.logging.Level
import java.util.logging.Logger

class SuperVanishListener(private val logger: Logger, private val plugin: Plugin) : Listener {
	@EventHandler(priority = MONITOR, ignoreCancelled = true)
	fun onPlayerVanishStateChange(e: PlayerVanishStateChangeEvent) {
		val player = plugin.server.getOfflinePlayer(e.uuid)
		val newState = ToggleableState.fromIsActiveBoolean(e.isVanishing)
		val oldState = !newState
		logPlayerStateChange(logger, Level.FINE, player, "VANISH", oldState.toString(), newState.toString())
	}
}
