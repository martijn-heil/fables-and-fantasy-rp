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
package com.fablesfantasyrp.plugin.chat

import me.neznamy.tab.api.TabAPI
import org.bukkit.ChatColor
import org.bukkit.entity.Player

class ChatReceptionIndicatorManager {
	fun start() {
		// Refresh interval must be divisible by 50
		// Identifier must start and end with %
		// Relational identifier must start with %rel_
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
