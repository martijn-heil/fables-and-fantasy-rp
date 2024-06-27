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
package com.fablesfantasyrp.plugin.text

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.Style
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender
import org.bukkit.command.ConsoleCommandSender
import org.bukkit.entity.Player

val CommandSender.nameStyle: Style
	get() = when (this) {
		is Player -> this.playerNameStyle
		is ConsoleCommandSender -> Style.style(NamedTextColor.DARK_RED)
		else -> Style.style().build()
	}

private fun processVaultPrefixSuffix(s: String): Component {
	val miniMessageLoose = MiniMessage.builder().strict(false).build()

	return s.let { ChatColor.translateAlternateColorCodes('&', it) }
			.let { legacyText(it) }
			.let { miniMessageLoose.serialize(it) }
			.let { it.replace("\\", "") }
			.let { miniMessageLoose.deserialize(it) }
}

val Player.prefix: Component
	get() = processVaultPrefixSuffix(vaultChat.getPlayerPrefix(this))

val Player.suffix: Component
	get() = processVaultPrefixSuffix(vaultChat.getPlayerSuffix(this))

val Player.playerNameStyle: Style
	get() {
		val miniMessageLoose = MiniMessage.builder().strict(false).build()

		return vaultChat.getPlayerPrefix(this)
				.let { ChatColor.translateAlternateColorCodes('&', it) }
				.let { legacyText(it) }
				.let { miniMessageLoose.serialize(it) }
				.let { it.replace("\\", "") }
				.let { "$it." }
				.let { miniMessageLoose.deserialize(it) }
				.let { it.children().lastOrNull() ?: it }
				.let { it.style() }
	}

fun Player.sendError(message: Component) {
	this.sendMessage(formatError(message))
}

fun CommandSender.sendError(message: Component) {
	this.sendMessage(formatError(message))
}

fun formatError(message: Component): Component {
	return miniMessage.deserialize("<red>Error:</red> <dark_red><message></dark_red>",
			Placeholder.component("message", message))
}

fun formatError(message: String): Component = formatError(Component.text(message))

fun CommandSender.sendError(message: String) = this.sendError(Component.text(message))
fun Player.sendError(message: String) = this.sendError(Component.text(message))
