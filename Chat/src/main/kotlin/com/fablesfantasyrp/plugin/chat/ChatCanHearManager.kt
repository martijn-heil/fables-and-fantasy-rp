package com.fablesfantasyrp.plugin.chat

import me.neznamy.tab.api.TabAPI
import org.bukkit.entity.Player

class ChatCanHearManager {
	fun start() {
		// Refresh interval must be divisible by 50
		// Identifier must start and end with %
		TabAPI.getInstance().placeholderManager.registerRelationalPlaceholder(
				"%rel_fables_chat_can_hear%",
				50) { viewer, target ->
			if (viewer == null || target == null) return@registerRelationalPlaceholder ""
			val viewerPlayer = viewer.player as Player
			val targetPlayer = target.player as Player
			val viewerChatData = viewerPlayer.chat
			return@registerRelationalPlaceholder if (viewerChatData.channel.getRecipients(viewerPlayer).contains(targetPlayer)) {
				"can hear"
			} else {
				"cannot hear"
			}
		}
	}
}
