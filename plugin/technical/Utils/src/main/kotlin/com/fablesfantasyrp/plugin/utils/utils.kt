package com.fablesfantasyrp.plugin.utils

import com.fablesfantasyrp.plugin.utils.extensions.bukkit.ItemStackCompanion
import com.fablesfantasyrp.plugin.utils.extensions.bukkit.fromBytes
import com.fablesfantasyrp.plugin.utils.extensions.bukkit.toBytes
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.Plugin
import java.io.Externalizable
import java.io.IOException
import java.io.ObjectInput
import java.io.ObjectOutput
import java.util.concurrent.locks.Lock
import kotlin.math.roundToLong


fun enforceDependencies(plugin: Plugin) {
	for (dependencyName in plugin.description.depend) {
		val dependency = plugin.server.pluginManager.getPlugin(dependencyName)
		if (dependency == null || !dependency.isEnabled) {
			throw IllegalStateException("Missing dependency '$dependencyName'")
		}
	}
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

fun quoteCommandArgument(s: String): String {
	return if (s.contains(" ") || s.contains("'")) "\"$s\"" else s
}
