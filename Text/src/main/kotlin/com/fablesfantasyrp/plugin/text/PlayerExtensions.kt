package com.fablesfantasyrp.plugin.text

import com.fablesfantasyrp.plugin.playerdata.FablesPlayer
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.Style
import net.kyori.adventure.text.minimessage.tag.Tag
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import org.bukkit.ChatColor
import org.bukkit.entity.Player

val FablesPlayer.playerNameStyle: Style
	get() = vaultChat.getPlayerPrefix(this.player)
				.let { ChatColor.translateAlternateColorCodes('&', it) }
				.let { ChatColor.getLastColors(it) }
				.let { legacyText(it).style() }

fun FablesPlayer.sendError(message: Component) {
	val customResolver = TagResolver.builder().tag("message", Tag.selfClosingInserting(message)).build()
	val finalMessage = miniMessage.deserialize("<red>Error:</red> <dark_red><message></dark_red>",
			TagResolver.standard(), customResolver)
	player.sendMessage(finalMessage)
}

fun FablesPlayer.sendError(message: String) {
	this.sendError(Component.text(message))
}

fun Player.sendError(message: String) = FablesPlayer.forPlayer(this).sendError(message)
fun Player.sendError(message: Component) = FablesPlayer.forPlayer(this).sendError(message)
