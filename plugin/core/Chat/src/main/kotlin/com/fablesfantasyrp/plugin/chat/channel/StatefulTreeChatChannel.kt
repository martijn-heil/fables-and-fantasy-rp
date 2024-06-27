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
package com.fablesfantasyrp.plugin.chat.channel

import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

abstract class StatefulTreeChatChannel(val from: CommandSender) : AbstractSubChanneledChatChannel("dm"), CommandSenderCompatibleChatChannel {
	var lastSubChannel: ChatChannel? = null

	override val default: ChatChannel?
		get() = lastSubChannel

	override val subChannels: Map<String, ChatChannel>
		get() = Bukkit.getOnlinePlayers().associate { Pair(it.name.uppercase(), ChatDirectMessage(from, it)) }

	override suspend fun sendMessage(from: Player, message: String) {
		val resolved = this.resolveSubChannelRecursive(message)
		val content = resolved.second
		val channel = resolved.first.let { if(it === this) (default) else it }
				?: throw ChatUnsupportedOperationException("Please /msg someone first before replying to them.")
		channel.sendMessage(from, content)
		lastSubChannel = channel
	}

	override fun sendMessage(from: CommandSender, message: String) {
		val resolved = this.resolveSubChannelRecursive(message)
		val content = resolved.second
		val channel = resolved.first.let { if(it === this) default else it }
				?: throw ChatUnsupportedOperationException("Please choose a subchannel.")

		if (channel !is CommandSenderCompatibleChatChannel)
			throw ChatUnsupportedOperationException("You must be a Player to chat in this channel")

		channel.sendMessage(from, content)
		lastSubChannel = channel
	}

	override fun getRecipients(from: CommandSender): Sequence<CommandSender>
		= (default as? CommandSenderCompatibleChatChannel)?.getRecipients(from) ?: emptySequence()
}
