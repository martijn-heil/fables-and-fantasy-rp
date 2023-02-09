package com.fablesfantasyrp.plugin.chat.data.entity

import com.fablesfantasyrp.plugin.chat.channel.ChatChannel
import com.fablesfantasyrp.plugin.chat.data.ChatPlayerData

interface ChatPlayerEntity : ChatPlayerData {
	fun doChat(message: String)
	fun doChat(rootChannel: ChatChannel, message: String)
	fun parseChatMessage(message: String): Pair<ChatChannel, String>
	fun parseChatMessage(rootChannel: ChatChannel, message: String): Pair<ChatChannel, String>
	fun cycleTypingAnimation()
	fun hasPermissionForChannel(channel: ChatChannel): Boolean
	fun mayChatIn(channel: ChatChannel): Boolean
}
