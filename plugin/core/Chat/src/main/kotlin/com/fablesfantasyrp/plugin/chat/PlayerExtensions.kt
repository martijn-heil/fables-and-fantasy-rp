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
	val message = this.promptChat(prompt, if (channel is PreviewableChatChannel) { { frunBlocking { channel.getPreview(this@awaitEmote, it) } } } else null)
	channel.sendMessage(this, message)
	chatEntity.channel = oldChannel
	return message
}

suspend fun Player.awaitEmote(prompt: Component, channel: ChatChannel = ChatInCharacter): String {
	val chatEntity = chat
	val oldChannel = chatEntity.channel
	chatEntity.channel = channel
	val message = this.promptChat(prompt, if (channel is PreviewableChatChannel) { { frunBlocking { channel.getPreview(this@awaitEmote, it) } } } else null)
	channel.sendMessage(this, message)
	chatEntity.channel = oldChannel
	return message
}
