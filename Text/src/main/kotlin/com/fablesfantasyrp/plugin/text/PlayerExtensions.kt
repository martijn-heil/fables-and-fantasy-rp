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
