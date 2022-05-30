package com.fablesfantasyrp.plugin.chat

import org.bukkit.Bukkit
import org.bukkit.Location
import kotlin.math.roundToLong

fun getPlayersWithinRange(from: Location, range: UInt) =
		Bukkit.getOnlinePlayers()
				.asSequence().filter {
					val to = it.location;
					to.world == from.world && from.distance(to).roundToLong().toUInt() <= range
				}
