package com.fablesfantasyrp.plugin.utils

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.ChatColor.*
import org.bukkit.Location
import org.bukkit.Server
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin
import java.util.*
import java.util.concurrent.locks.Lock
import kotlin.math.roundToLong

// This code is supported by SuperVanish, PremiumVanish, VanishNoPacket and a few more vanish plugins.
val Player.isVanished: Boolean
	get() = getMetadata("vanished").find { it.asBoolean() } != null



const val DISTANCE_WHISPER = 2U
const val DISTANCE_QUIET = 8u
const val DISTANCE_TALK = 15U
const val DISTANCE_SHOUT = 30U

const val DISTANCE_WHISPER = 2U
const val DISTANCE_QUIET = 15U
const val DISTANCE_TALK = 15U
const val DISTANCE_SHOUT = 30U

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
	return if (s.contains(" ") || s.contains("'")) "\"$s\"" else s
}

data class BlockCoordinates(val x: Int, val y: Int, val z: Int) {
	fun withWorld(world: UUID) = BlockIdentifier(world, x, y, z)
}
data class BlockIdentifier(val world: UUID, val x: Int, val y: Int, val z: Int) {
	fun toBlockCoordinates() = BlockCoordinates(x, y, z)
	fun toLocation() = Location(Bukkit.getWorld(world)!!, x.toDouble(), y.toDouble(), z.toDouble())
}

fun Location.toBlockIdentifier() = BlockIdentifier(this.world.uid, this.blockX, this.blockY, this.blockZ)

fun ChatColor.toNamedTextColor(): NamedTextColor? = when (this) {
	BLACK -> NamedTextColor.BLACK
	DARK_BLUE -> NamedTextColor.DARK_BLUE
	DARK_GREEN -> NamedTextColor.DARK_GREEN
	DARK_AQUA -> NamedTextColor.DARK_AQUA
	DARK_RED -> NamedTextColor.DARK_RED
	DARK_PURPLE -> NamedTextColor.DARK_PURPLE
	GOLD -> NamedTextColor.GOLD
	GRAY -> NamedTextColor.GRAY
	DARK_GRAY -> NamedTextColor.DARK_GRAY
	BLUE -> NamedTextColor.BLUE
	GREEN -> NamedTextColor.GREEN
	AQUA -> NamedTextColor.AQUA
	RED -> NamedTextColor.RED
	LIGHT_PURPLE -> NamedTextColor.LIGHT_PURPLE
	YELLOW -> NamedTextColor.YELLOW
	WHITE -> NamedTextColor.WHITE
	MAGIC -> null
	BOLD -> null
	STRIKETHROUGH -> null
	UNDERLINE -> null
	ITALIC -> null
	RESET -> null
}
