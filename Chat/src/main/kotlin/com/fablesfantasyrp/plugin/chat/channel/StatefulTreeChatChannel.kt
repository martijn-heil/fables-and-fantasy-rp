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

	override fun sendMessage(from: Player, message: String) {
		val resolved = this.resolveSubChannelRecursive(message)
		val content = resolved.second
		val channel = resolved.first.let { if(it === this) default else it }
				?: throw ChatUnsupportedOperationException("Please choose a subchannel.")
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
