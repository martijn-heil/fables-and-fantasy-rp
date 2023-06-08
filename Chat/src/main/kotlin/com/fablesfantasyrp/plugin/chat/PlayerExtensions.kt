package com.fablesfantasyrp.plugin.chat

import com.fablesfantasyrp.plugin.chat.channel.ChatInCharacter
import com.fablesfantasyrp.plugin.chat.data.entity.ChatPlayer
import com.fablesfantasyrp.plugin.chat.data.entity.EntityChatPlayerRepository
import com.fablesfantasyrp.plugin.form.promptChat
import net.kyori.adventure.text.Component
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player
import org.koin.core.context.GlobalContext

val OfflinePlayer.chat: ChatPlayer
	get() = GlobalContext.get().get<EntityChatPlayerRepository>().forId(uniqueId)!!

suspend fun Player.awaitEmote(prompt: String): String {
	val chatEntity = chat
	val oldChannel = chatEntity.channel
	chatEntity.channel = ChatInCharacter
	val message = this.promptChat(prompt)
	ChatInCharacter.sendMessage(this, message)
	chatEntity.channel = oldChannel
	return message
}

suspend fun Player.awaitEmote(prompt: Component): String {
	val chatEntity = chat
	val oldChannel = chatEntity.channel
	chatEntity.channel = ChatInCharacter
	val message = this.promptChat(prompt)
	ChatInCharacter.sendMessage(this, message)
	chatEntity.channel = oldChannel
	return message
}
