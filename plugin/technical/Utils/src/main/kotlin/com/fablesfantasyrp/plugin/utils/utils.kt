/*
 * Fables and Fantasy RP kotlin plugins.
 * Copyright (C) 2024  Martijn Heil
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.fablesfantasyrp.plugin.utils

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.plugin.Plugin
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
