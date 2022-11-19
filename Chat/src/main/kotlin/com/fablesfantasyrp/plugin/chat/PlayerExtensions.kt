package com.fablesfantasyrp.plugin.chat

import com.fablesfantasyrp.plugin.chat.channel.ChatInCharacter
import com.fablesfantasyrp.plugin.chat.data.entity.ChatPlayerEntity
import com.fablesfantasyrp.plugin.form.promptChat
import net.kyori.adventure.text.Component
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player

val OfflinePlayer.chat: ChatPlayerEntity
	get() = chatPlayerDataManager.forId(uniqueId)!!

suspend fun Player.awaitEmote(prompt: String) {
	val chatEntity = chat
	val oldChannel = chatEntity.channel
	chatEntity.channel = ChatInCharacter
	val message = this.promptChat(prompt)
	ChatInCharacter.sendMessage(this, message)
	chatEntity.channel = oldChannel
}

suspend fun Player.awaitEmote(prompt: Component) {
	val chatEntity = chat
	val oldChannel = chatEntity.channel
	chatEntity.channel = ChatInCharacter
	val message = this.promptChat(prompt)
	ChatInCharacter.sendMessage(this, message)
	chatEntity.channel = oldChannel
}
