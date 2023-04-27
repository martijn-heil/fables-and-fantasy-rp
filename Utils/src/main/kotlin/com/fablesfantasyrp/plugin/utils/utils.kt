package com.fablesfantasyrp.plugin.utils

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.*
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin
import java.util.*
import java.util.concurrent.locks.Lock
import kotlin.math.roundToLong

// This code is supported by SuperVanish, PremiumVanish, VanishNoPacket and a few more vanish plugins.
val Player.isVanished: Boolean
	get() = getMetadata("vanished").find { it.asBoolean() } != null

val FABLES_ADMIN = Bukkit.getOfflinePlayer(UUID.fromString("bcdb5a59-269e-43df-914b-eed888597272"))
val SPAWN: Location = EDEN!!.spawnLocation
val FLATROOM: World? get() = Bukkit.getWorld("flatroom")
val PLOTS: World? get() = Bukkit.getWorld("plots")
val EDEN: World? get() = Bukkit.getWorld("Eden")

fun enforceDependencies(plugin: Plugin) {
	for (dependencyName in plugin.description.depend) {
		val dependency = plugin.server.pluginManager.getPlugin(dependencyName)
		if (dependency == null || !dependency.isEnabled) {
			throw IllegalStateException("Missing dependency '$dependencyName'")
		}
	}
}

fun Location.distanceSafe(to: Location): Double {
	if (this.world != to.world) return Double.MAX_VALUE
	return this.distance(to)
}

fun Server.broadcast(location: Location, range: Int, message: Component, toConsoleSender: Boolean = true) {
	getPlayersWithinRange(location, range.toUInt()).forEach { it.sendMessage(message) }
	if (toConsoleSender) consoleSender.sendMessage(message)
}

fun Server.broadcast(location: Location, range: Int, message: String, toConsoleSender: Boolean = true) {
	getPlayersWithinRange(location, range.toUInt()).forEach { it.sendMessage(message) }
	if (toConsoleSender) consoleSender.sendMessage(message)
}

fun getPlayersWithinRange(from: Location, range: UInt) =
		Bukkit.getOnlinePlayers()
				.asSequence().filter {
					val to = it.location;
					to.world == from.world && from.distance(to).roundToLong().toUInt() <= range
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

fun ChatColor.toNamedTextColor(): NamedTextColor? = when (this) {
	ChatColor.BLACK -> NamedTextColor.BLACK
	ChatColor.DARK_BLUE -> NamedTextColor.DARK_BLUE
	ChatColor.DARK_GREEN -> NamedTextColor.DARK_GREEN
	ChatColor.DARK_AQUA -> NamedTextColor.DARK_AQUA
	ChatColor.DARK_RED -> NamedTextColor.DARK_RED
	ChatColor.DARK_PURPLE -> NamedTextColor.DARK_PURPLE
	ChatColor.GOLD -> NamedTextColor.GOLD
	ChatColor.GRAY -> NamedTextColor.GRAY
	ChatColor.DARK_GRAY -> NamedTextColor.DARK_GRAY
	ChatColor.BLUE -> NamedTextColor.BLUE
	ChatColor.GREEN -> NamedTextColor.GREEN
	ChatColor.AQUA -> NamedTextColor.AQUA
	ChatColor.RED -> NamedTextColor.RED
	ChatColor.LIGHT_PURPLE -> NamedTextColor.LIGHT_PURPLE
	ChatColor.YELLOW -> NamedTextColor.YELLOW
	ChatColor.WHITE -> NamedTextColor.WHITE
	ChatColor.MAGIC -> null
	ChatColor.BOLD -> null
	ChatColor.STRIKETHROUGH -> null
	ChatColor.UNDERLINE -> null
	ChatColor.ITALIC -> null
	ChatColor.RESET -> null
}
