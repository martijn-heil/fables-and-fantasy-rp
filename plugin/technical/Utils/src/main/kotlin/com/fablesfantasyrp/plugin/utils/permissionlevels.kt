package com.fablesfantasyrp.plugin.utils

import net.kyori.adventure.text.Component
import org.bukkit.Server
import org.bukkit.permissions.Permissible

fun Permissible.hasPermissionLevel(basePermission: String, maxLevel: Int, level: Int)
	= if (level == 0) true else (level.. maxLevel).any { this.hasPermission("$basePermission.$it") }

fun Permissible.getPermissionLevel(basePermission: String, maxLevel: Int): Int
	= (maxLevel downTo 1).firstOrNull { this.hasPermission("$basePermission.$it") } ?: 0

fun Server.broadcastToPermissionLevel(basePermission: String, maxLevel: Int, level: Int, message: Component)
	= this.onlinePlayers
		.asSequence()
		.filter { it.hasPermissionLevel(basePermission, maxLevel, level) }
		.plus(this.consoleSender)
		.forEach { it.sendMessage(message) }
