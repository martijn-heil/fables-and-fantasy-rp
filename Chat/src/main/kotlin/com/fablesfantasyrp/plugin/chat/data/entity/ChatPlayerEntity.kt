package com.fablesfantasyrp.plugin.chat.data.entity

import com.fablesfantasyrp.plugin.chat.channel.ChatChannel
import com.fablesfantasyrp.plugin.chat.data.ChatPlayerData

interface ChatPlayerEntity : ChatPlayerData {
	fun doChat(message: String)
	fun parseChatMessage(message: String): Pair<ChatChannel, String>
	fun cycleTypingAnimation()
}
