package com.fablesfantasyrp.plugin.chat

import com.denizenscript.denizencore.objects.core.ElementTag
import com.fablesfantasyrp.plugin.chat.channel.*
import com.fablesfantasyrp.plugin.denizeninterop.dFlags
import com.fablesfantasyrp.plugin.playerdata.FablesOfflinePlayer
import com.fablesfantasyrp.plugin.playerdata.FablesPlayer
import me.neznamy.tab.api.TabAPI
import me.neznamy.tab.api.team.UnlimitedNametagManager
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
	val result: Pair<ChatChannel, String> = this.parseChatMessage(message)
	result.first.sendMessage(player, result.second)
}

fun FablesPlayer.parseChatMessage(message: String): Pair<ChatChannel, String> {
	val channelRegex = Regex("^\\s*\\$([A-z.]+)( (.*))?")
	val matchResult = channelRegex.matchEntire(message)
	return if (matchResult != null) {
		val channelName = matchResult.groupValues[1]
		ChatChannel.fromStringAliased(channelName)?.let { Pair(it, matchResult.groupValues[3]) }
				?: throw ChatIllegalArgumentException("Unknown global channel '$channelName'.")
	} else {
		Pair(chatChannel, message)
	}
}

var FablesPlayer.isTyping: Boolean
	get() = rawData.isTyping
	internal set(value) {
		if (value == rawData.isTyping) return
		rawData.isTyping = value
		val tabPlayer = TabAPI.getInstance().getPlayer(player.uniqueId) ?: return
		val teamManager = TabAPI.getInstance().teamManager as? UnlimitedNametagManager ?: return
		if (value) {
			teamManager.setLine(tabPlayer, "belowname", ChatColor.GRAY.toString() + ".")
			this.cycleTypingAnimation()
		} else {
			rawData.lastTypingAnimation = null
			teamManager.resetLine(tabPlayer, "belowname")
		}
	}

internal fun FablesPlayer.cycleTypingAnimation() {
	val animation = when (rawData.lastTypingAnimation) {
		"." -> ".."
		".." -> "..."
		"..." -> "...."
		"...." -> "."
		else -> "."
	}
	val tabPlayer = TabAPI.getInstance().getPlayer(player.uniqueId) ?: return
	val teamManager = TabAPI.getInstance().teamManager as? UnlimitedNametagManager ?: return
	teamManager.setLine(tabPlayer, "belowname", ChatColor.WHITE.toString() + animation)
	rawData.lastTypingAnimation = animation
}
