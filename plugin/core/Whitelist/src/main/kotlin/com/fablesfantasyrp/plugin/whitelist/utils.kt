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
package com.fablesfantasyrp.plugin.whitelist

import com.fablesfantasyrp.plugin.chat.channel.ChatSpectator
import com.fablesfantasyrp.plugin.chat.chat
import com.fablesfantasyrp.plugin.text.miniMessage
import com.fablesfantasyrp.plugin.text.parseLinks
import com.fablesfantasyrp.plugin.text.playerNameStyle
import com.fablesfantasyrp.plugin.utils.extensions.bukkit.isVanished
import de.myzelyam.api.vanish.VanishAPI
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.Style
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

fun sendWelcomeMessage(who: CommandSender) {
	val message = miniMessage.deserialize(
			"<gray>" +
					"Welcome to <green><bold>Fables & Fantasy</bold></green>!<newline>" +
					"<newline>" +
					"In order to play, you must apply for <yellow>Whitelist Access</yellow>!<newline>" +
					"This is a <yellow>very quick and simple</yellow> process!<newline>" +
					"Your application will be reviewed <yellow>in 1 day or less</yellow>.<newline>" +
					"<whitelist_application_link><newline>" +
					"<newline>" +
					"If you have <yellow>any questions</yellow>, please feel free to ask on our <yellow>Discord</yellow>!<newline>" +
					"<discord_link><newline>" +
					"<yellow>Type in chat</yellow> to talk with other spectators and staff.<newline>" +
					"Please enjoy your stay in the world of Eden!" +
					"</gray>",
			Placeholder.component("whitelist_application_link",
					parseLinks("https://forums.fablesfantasyrp.com/index.php?form/whitelist-application.1/select")),
			Placeholder.component("discord_link", parseLinks("https://discord.gg/ymNFxDKPx9"))
	)
	who.sendMessage(message)
}

data class ScopedMessage(val recipients: Collection<CommandSender>?, val message: Component)

private fun joinLeavePrefix(symbol: Component)
	= miniMessage.deserialize("<dark_gray>[</dark_gray><symbol><dark_gray>]</dark_gray>",
			Placeholder.component("symbol", symbol))
private fun leavePrefix() = joinLeavePrefix(Component.text("-").color(NamedTextColor.RED))
private fun joinPrefix() = joinLeavePrefix(Component.text("+").color(NamedTextColor.GREEN))

private fun joinQuitMessage(p: Player, isJoin: Boolean, isSilent: Boolean): ScopedMessage? {
	val playerNameStyle = if (p.isWhitelisted) p.playerNameStyle else Style.style(NamedTextColor.GRAY)
	val silentSuffix = Component.text(" (silent)").color(NamedTextColor.GREEN)
	val spectatorSuffix = Component.text( " (spectator)").color(NamedTextColor.LIGHT_PURPLE)
	val isSpectator = !p.isWhitelisted

	val message = miniMessage.deserialize("<prefix> <name><spectator><silent>",
			Placeholder.component("name", p.name().style(playerNameStyle)),
			Placeholder.component("prefix", if (isJoin ) joinPrefix() else leavePrefix()),
			Placeholder.component("spectator", if (isSpectator) spectatorSuffix else Component.empty()),
			Placeholder.component("silent", if (isSilent) silentSuffix else Component.empty())
	)

	val recipients = when {
		isSilent -> Bukkit.getOnlinePlayers()
				.filter { it == p || VanishAPI.canSee(it, p) }
				.plus(Bukkit.getConsoleSender())
		isSpectator -> Bukkit.getOnlinePlayers()
				.filter { !it.chat.disabledChannels.contains(ChatSpectator) }
		else -> null
	}

	return ScopedMessage(recipients, message)
}

fun joinMessage(p: Player, isSilent: Boolean = p.hasPermission(Permission.SilentJoinQuit))
	= joinQuitMessage(p, true, isSilent)

fun quitMessage(p: Player, isSilent: Boolean = p.isVanished && p.hasPermission(Permission.SilentJoinQuit))
	= joinQuitMessage(p, false, isSilent)
