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
