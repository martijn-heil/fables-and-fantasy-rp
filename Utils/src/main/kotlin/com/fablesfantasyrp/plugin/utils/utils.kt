package com.fablesfantasyrp.plugin.utils

import com.earth2me.essentials.User
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.OfflinePlayer
import org.bukkit.World
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin
import java.util.*
import java.util.concurrent.locks.Lock

// This code is supported by SuperVanish, PremiumVanish, VanishNoPacket and a few more vanish plugins.
val Player.isVanished: Boolean
	get() = getMetadata("vanished").find { it.asBoolean() } != null

val OfflinePlayer.ess: User
	get() = essentials.getUser(uniqueId)

val FABLES_ADMIN = Bukkit.getOfflinePlayer(UUID.fromString("bcdb5a59-269e-43df-914b-eed888597272"))
val SPAWN: Location = essentialsSpawn.getSpawn("default")
val FLATROOM_UUID = UUID.fromString("87DE3233893348B4B745D6930BD92EBD")
val FLATROOM: World? = Bukkit.getWorld(FLATROOM_UUID)
val PLOTS_UUID = UUID.fromString("3A542ECF33DB4D3DAC73421F4EAF65E6")
val PLOTS: World? = Bukkit.getWorld(PLOTS_UUID)

fun enforceDependencies(plugin: Plugin) {
	for (dependencyName in plugin.description.depend) {
		val dependency = plugin.server.pluginManager.getPlugin(dependencyName)
		if (dependency == null || !dependency.isEnabled) {
			throw IllegalStateException("Missing dependency '$dependencyName'")
		}
	}
}

fun Boolean.asEnabledDisabledComponent(): Component
	= if (this) {
		Component.text("enabled").color(NamedTextColor.GREEN)
	} else {
		Component.text("disabled").color(NamedTextColor.RED)
	}

fun<T> Lock.withLock(f: () -> T): T {
	this.lock()
	return try {
		f()
	} finally {
		this.unlock()
	}
}

fun Location.humanReadable() = "${blockX},${blockY},${blockZ},${world.name}"

// Citizens NPC's are instances of Player too, but obviously, not real players
// Citizens NPC's don't show up in the server.onlinePlayers list
val Player.isRealPlayer: Boolean get() = server.onlinePlayers.contains(player)

fun quoteCommandArgument(s: String): String {
	return if (s.contains(" ")) "\"$s\"" else s
}

data class BlockLocation(val x: Int, val y: Int, val z: Int)
