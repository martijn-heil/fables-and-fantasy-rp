package com.fablesfantasyrp.plugin.utils

import com.earth2me.essentials.User
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin

// This code is supported by SuperVanish, PremiumVanish, VanishNoPacket and a few more vanish plugins.
val Player.isVanished: Boolean
	get() = getMetadata("vanished").find { it.asBoolean() } != null

val OfflinePlayer.ess: User
	get() = essentials.getUser(uniqueId)

fun enforceDependencies(plugin: Plugin) {
	for (dependencyName in plugin.description.depend) {
		val dependency = plugin.server.pluginManager.getPlugin(dependencyName)
		if (dependency == null || !dependency.isEnabled) {
			throw IllegalStateException("Missing dependency '$dependencyName'")
		}
	}
}
