package com.fablesfantasyrp.plugin.chat.channel

import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

abstract class StatefulTreeChatChannel(val from: CommandSender) : AbstractSubChanneledChatChannel("dm") {
	var lastSubChannel: ChatChannel? = null

	override val default: ChatChannel?
		get() = lastSubChannel

	override val subChannels: Map<String, ChatChannel>
		get() = Bukkit.getOnlinePlayers().associate { Pair(it.name.uppercase(), DirectMessage(from, it)) }

	override fun sendMessage(from: Player, message: String) {
		val resolved = this.resolveSubChannelRecursive(message)
		val content = resolved.second
		val channel = resolved.first.let { if(it === this) default else it }
				?: throw ChatUnsupportedOperationException("Please choose a subchannel.")
		channel.sendMessage(from, content)
		lastSubChannel = channel
	}
}
