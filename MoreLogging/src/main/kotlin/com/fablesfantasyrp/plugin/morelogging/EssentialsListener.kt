package com.fablesfantasyrp.plugin.morelogging

import com.fablesfantasyrp.plugin.utils.ToggleableState
import net.ess3.api.IUser
import net.ess3.api.events.FlyStatusChangeEvent
import net.ess3.api.events.GodStatusChangeEvent
import org.bukkit.OfflinePlayer
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority.MONITOR
import org.bukkit.event.Listener
import org.bukkit.plugin.Plugin
import java.util.logging.Level
import java.util.logging.Logger

class EssentialsListener(private val logger: Logger, private val plugin: Plugin) : Listener {
	private val IUser.offlinePlayer: OfflinePlayer
		get() = plugin.server.getOfflinePlayer(name)

	@EventHandler(priority = MONITOR, ignoreCancelled = true)
	fun onGodStatusChange(e: GodStatusChangeEvent) {
		val player = e.affected.offlinePlayer
		val newState = ToggleableState.fromIsActiveBoolean(e.value)
		val oldState = !newState
		logPlayerStateChange(logger, Level.FINE, player, "GOD", oldState.toString(), newState.toString())
	}

	@EventHandler(priority = MONITOR, ignoreCancelled = true)
	fun onFlyStatusChange(e: FlyStatusChangeEvent) {
		val player = e.affected.offlinePlayer
		val newState = ToggleableState.fromIsActiveBoolean(e.value)
		val oldState = !newState
		logPlayerStateChange(logger, Level.FINE, player, "FLY", oldState.toString(), newState.toString())
	}
}
