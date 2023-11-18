package com.fablesfantasyrp.plugin.morelogging

import com.fablesfantasyrp.plugin.utils.extensions.bukkit.humanReadable
import org.bukkit.OfflinePlayer
import java.util.logging.Level
import java.util.logging.Logger

fun logPlayerStateChange(logger: Logger, level: Level, p: OfflinePlayer, name: String, oldState: String, newState: String) =
		logger.log(level,"[${p.player?.location?.humanReadable()}] ${p.name}: $name $oldState -> $newState")
