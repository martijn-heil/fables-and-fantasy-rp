package com.fablesfantasyrp.plugin.chat

import com.fablesfantasyrp.plugin.chat.channel.ChatChannel
import com.fablesfantasyrp.plugin.chat.channel.ChatInCharacter
import com.fablesfantasyrp.plugin.chat.channel.PreviewableChatChannel
import com.fablesfantasyrp.plugin.chat.data.entity.ChatPlayer
import com.fablesfantasyrp.plugin.chat.data.entity.EntityChatPlayerRepository
import com.fablesfantasyrp.plugin.form.promptChat
import net.kyori.adventure.text.Component
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player
import org.koin.core.context.GlobalContext

val OfflinePlayer.chat: ChatPlayer
	get() = GlobalContext.get().get<EntityChatPlayerRepository>().forId(uniqueId)!!

suspend fun Player.awaitEmote(prompt: String, channel: ChatChannel = ChatInCharacter): String {
	val chatEntity = chat
	val oldChannel = chatEntity.channel
	chatEntity.channel = channel
	val message = this.promptChat(prompt, if (channel is PreviewableChatChannel) { { channel.getPreview(this, it) } } else null)
	channel.sendMessage(this, message)
	chatEntity.channel = oldChannel
	return message
}

suspend fun Player.awaitEmote(prompt: Component, channel: ChatChannel = ChatInCharacter): String {
	val chatEntity = chat
	val oldChannel = chatEntity.channel
	chatEntity.channel = channel
	val message = this.promptChat(prompt, if (channel is PreviewableChatChannel) { { channel.getPreview(this, it) } } else null)
	channel.sendMessage(this, message)
	chatEntity.channel = oldChannel
	return message
}
