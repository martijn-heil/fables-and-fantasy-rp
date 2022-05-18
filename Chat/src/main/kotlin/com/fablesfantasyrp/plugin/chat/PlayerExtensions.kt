package com.fablesfantasyrp.plugin.chat

import com.denizenscript.denizencore.objects.core.ElementTag
import com.fablesfantasyrp.plugin.denizeninterop.dFlags
import com.fablesfantasyrp.plugin.playerdata.FablesOfflinePlayer
import com.fablesfantasyrp.plugin.playerdata.FablesPlayer
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.Style
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import org.bukkit.ChatColor

var FablesOfflinePlayer.chatChannel: ChatChannel
	get() = when {
		!offlinePlayer.isWhitelisted -> ChatSpectator
		else -> ChatChannel.fromString(rawData.chatChannel)!!
	}
	set(value) {
		rawData.chatChannel = value.toString()
		player?.sendMessage("$SYSPREFIX Your chat channel has been switched to ${value.toString().uppercase()}!")
	}

var FablesOfflinePlayer.chatStyle: Style?
	get() = rawData.chatStyle
	set(value) {
		rawData.chatStyle = value

		// Rolls uses the flag read-only, so until we have ported Rolls over we will just keep updating the flag
		if (value != null) {
			val legacyText = ChatColor.getLastColors(LegacyComponentSerializer.legacySection()
					.serialize(Component.text("nil").style(value)))
			offlinePlayer.dFlags.setFlag("chat_color", ElementTag(legacyText), null)
		} else {
			offlinePlayer.dFlags.setFlag("chat_color", null, null)
		}
	}

var FablesOfflinePlayer.disabledChatChannels: Set<ToggleableChatChannel>
	get() = rawData.chatDisabledChannels
			.map { ChatChannel.fromString(it) ?: throw IllegalStateException() }
			.map { (it as? ToggleableChatChannel) ?: throw IllegalStateException() }
			.toSet()
	set(value) { rawData.chatDisabledChannels = value.map { it.toString() }.toSet() }

fun FablesPlayer.doChat(message: String) {
	chatChannel.sendMessage(player, message)
}

val FablesPlayer.playerNameStyle: Style
	get() = vaultChat.getPlayerPrefix(this.player)
				.let { ChatColor.translateAlternateColorCodes('&', it) }
				.let { ChatColor.getLastColors(it) }
				.let { legacyText(it).style() }
