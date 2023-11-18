package com.fablesfantasyrp.plugin.chat.channel

import net.kyori.adventure.text.Component
import org.bukkit.entity.Player

abstract class AbstractSubChanneledChatChannel(private val name: String)
	: ChatChannel, PreviewableChatChannel, SubChanneledChatChannel {
	abstract val default: ChatChannel?

	@Transient
	private val pattern = "^#(\\.?[A-z0-9_]+)\\s?(.*)$"

	abstract val subChannels: Map<String, ChatChannel>

	override fun getRecipients(from: Player): Sequence<Player> = default?.getRecipients(from) ?: emptySequence()

	override fun sendMessage(from: Player, message: String) {
		val resolved = this.resolveSubChannelRecursive(message)
		val content = resolved.second
		val channel = resolved.first.let { if(it === this) default else it }
				?: throw ChatUnsupportedOperationException("Please choose a subchannel.")
		channel.sendMessage(from, content)
	}

	override fun getPreview(from: Player, message: String): Component {
		val resolved = this.resolveSubChannelRecursive(message)
		val content = resolved.second
		val channel = (resolved.first as? PreviewableChatChannel)
				?.let { if (it === this) default as? PreviewableChatChannel else it }
				?: return Component.text("")
		return channel.getPreview(from, content)
	}

	override fun resolveSubChannel(message: String): Pair<ChatChannel, String> {
		val match = Regex(pattern).matchEntire(message) ?: return Pair(this, message)

		val channelName = match.groups[1]!!.value.uppercase()
		val messageContent = match.groups[2]!!.value
		val channel = subChannels[channelName] ?: throw ChatIllegalArgumentException("Unknown relative channel '$channelName'.")
		return Pair(channel, messageContent)
	}

	fun resolveSubChannelForName(name: String) = subChannels[name.uppercase()]

	override fun toString() = name
}
