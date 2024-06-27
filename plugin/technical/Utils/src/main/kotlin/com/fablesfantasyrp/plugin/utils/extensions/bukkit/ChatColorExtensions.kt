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
package com.fablesfantasyrp.plugin.utils.extensions.bukkit

import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.ChatColor

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
