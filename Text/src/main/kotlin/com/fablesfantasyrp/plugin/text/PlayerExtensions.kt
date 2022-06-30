package com.fablesfantasyrp.plugin.text

import com.fablesfantasyrp.plugin.playerdata.FablesPlayer
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.Style
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender
import org.bukkit.command.ConsoleCommandSender
import org.bukkit.entity.Player

val CommandSender.nameStyle: Style
	get() = when (this) {
		is Player -> FablesPlayer.forPlayer(this).playerNameStyle
		is ConsoleCommandSender -> Style.style(NamedTextColor.DARK_RED)
		else -> Style.style().build()
	}

val FablesPlayer.playerNameStyle: Style
	get() = vaultChat.getPlayerPrefix(this.player)
				.let { ChatColor.translateAlternateColorCodes('&', it) }
				.let { ChatColor.getLastColors(it) }
				.let { legacyText(it).style() }

fun FablesPlayer.sendError(message: Component) {
	val finalMessage = miniMessage.deserialize("<red>Error:</red> <dark_red><message></dark_red>",
			Placeholder.component("message", message))
	player.sendMessage(finalMessage)
}

fun CommandSender.sendError(message: Component) {
	val finalMessage = miniMessage.deserialize("<red>Error:</red> <dark_red><message></dark_red>",
			Placeholder.component("message", message))
	this.sendMessage(finalMessage)
}

fun CommandSender.sendError(message: String) = this.sendError(Component.text(message))
fun FablesPlayer.sendError(message: String) = this.sendError(Component.text(message))
fun Player.sendError(message: String) = FablesPlayer.forPlayer(this).sendError(message)
fun Player.sendError(message: Component) = FablesPlayer.forPlayer(this).sendError(message)
