package com.fablesfantasyrp.plugin.morelogging

import org.bukkit.Location
import org.bukkit.OfflinePlayer
import java.util.logging.Logger

internal fun Location.humanReadable() = "${blockX},${blockY},${blockZ},${world.name}"

internal fun logPlayerStateChange(logger: Logger, p: OfflinePlayer, name: String, oldState: String, newState: String) =
		logger.info("[${p.player?.location?.humanReadable()}] ${p.name}: $name $oldState -> $newState")
