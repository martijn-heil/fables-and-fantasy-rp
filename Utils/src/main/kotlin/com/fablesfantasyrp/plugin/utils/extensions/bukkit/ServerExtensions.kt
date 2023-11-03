package com.fablesfantasyrp.plugin.utils.extensions.bukkit

import com.fablesfantasyrp.plugin.utils.getPlayersWithinRange
import net.kyori.adventure.text.Component
import org.bukkit.Location
import org.bukkit.Server

fun Server.broadcast(location: Location, range: Int, message: Component, toConsoleSender: Boolean = true) {
	getPlayersWithinRange(location, range.toUInt()).forEach { it.sendMessage(message) }
	if (toConsoleSender) consoleSender.sendMessage(message)
}

fun Server.broadcast(location: Location, range: Int, message: String, toConsoleSender: Boolean = true) {
	getPlayersWithinRange(location, range.toUInt()).forEach { it.sendMessage(message) }
	if (toConsoleSender) consoleSender.sendMessage(message)
}
