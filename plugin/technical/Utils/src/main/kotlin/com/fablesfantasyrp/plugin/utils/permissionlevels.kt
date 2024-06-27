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
