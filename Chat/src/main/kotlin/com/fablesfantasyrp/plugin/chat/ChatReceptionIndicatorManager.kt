package com.fablesfantasyrp.plugin.chat

import me.neznamy.tab.api.TabAPI
import org.bukkit.ChatColor
import org.bukkit.entity.Player

class ChatReceptionIndicatorManager {
	fun start() {
		// Refresh interval must be divisible by 50
		// Identifier must start and end with %
		TabAPI.getInstance().placeholderManager.registerRelationalPlaceholder(
				"%rel_fables_chat_reception_indicator%",
				50) { viewer, target ->
			if (viewer == null || target == null) return@registerRelationalPlaceholder "ERROR"
			val viewerPlayer = viewer.player as Player
			val targetPlayer = target.player as Player
			val viewerChatData = viewerPlayer.chat

			if (!viewerChatData.isReceptionIndicatorEnabled) return@registerRelationalPlaceholder ""

			val channel = viewerChatData.previewChannel ?: viewerChatData.channel

			return@registerRelationalPlaceholder if (channel.getRecipients(viewerPlayer).contains(targetPlayer)) {
				"${ChatColor.GREEN}\uD83D\uDC42"
			} else {
				""
			}
		}
	}
}
