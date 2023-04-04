package com.fablesfantasyrp.plugin.text

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.event.HoverEvent
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import org.bukkit.ChatColor
import java.net.URL

fun formatChat(message: String) = message
		.replace("&l", "${ChatColor.BOLD}")
		.replace("&o", "${ChatColor.ITALIC}")
		.replace("&r", "${ChatColor.RESET}")

fun parseLinks(message: String): Component =
		Component.text().append(message.split(" ").map {
			if (it.matches(Regex("^https?://.*"))) {
				val isSafe = it.startsWith("https://")
				val safetyColor = if (isSafe) NamedTextColor.GREEN else NamedTextColor.YELLOW
				Component.text("[ LINK ]")
						.color(safetyColor)
						.decorate(TextDecoration.BOLD)
						.clickEvent(ClickEvent.openUrl(URL(it))).asComponent()
						.hoverEvent(HoverEvent.showText(
								Component.text()
										.append(Component.text(it).color(safetyColor))
										.append(Component.newline())
										.append(
												if (isSafe)
													Component.text("☑ HTTPS Encrypted").color(NamedTextColor.GREEN)
												else
													Component.text()
															.append(Component.text("× No HTTPS Encryption").color(NamedTextColor.YELLOW))
															.append(Component.newline())
															.append(Component.text("Do not put any personal information on this webpage.").color(NamedTextColor.YELLOW))
										)
						))
			} else Component.text(it)
		}.asSequence().join(Component.text(" ")).asIterable()).asComponent().compact()

fun legacyText(legacyChat: String) = LegacyComponentSerializer.legacySection().deserialize(legacyChat)

fun<T> Sequence<T>.join(separator: T) =
	this.mapIndexed { index, value -> if(index != 0) listOf(separator, value) else listOf(value) }.flatten()

fun isSaneName(name: String): Boolean {
	return !Regex("[^A-Za-z0-9\\- ']").containsMatchIn(name)
}

val CHAT_COLORS = ChatColor.values().toList().minus(listOf(
	ChatColor.BOLD,
	ChatColor.ITALIC,
	ChatColor.STRIKETHROUGH,
	ChatColor.MAGIC,
	ChatColor.RESET,
	ChatColor.UNDERLINE
))
