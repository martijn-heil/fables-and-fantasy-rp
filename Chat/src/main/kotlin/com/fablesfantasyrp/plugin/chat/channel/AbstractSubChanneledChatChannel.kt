package com.fablesfantasyrp.plugin.chat.channel

import net.kyori.adventure.text.Component
import org.bukkit.entity.Player
import java.io.Serializable

abstract class AbstractSubChanneledChatChannel(private val name: String,
											   private val map: Map<String, ChatChannel>,
											   private val default: ChatChannel)
	: ChatChannel, PreviewableChatChannel, SubChanneledChatChannel, Serializable {
	@Transient
	private val pattern = "^#([A-z]+)\\s?(.*)$"
	//private val pattern = "^\\s*#([A-z]+)\\s?(.*)$"

	val subChannels
		get() = map.values

	override fun sendMessage(from: Player, message: String) {
		val resolved = this.resolveSubChannelRecursive(message)
		val content = resolved.second
		val channel = resolved.first.let { if(it === this) default else it }
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
		val channel = map[channelName] ?: throw ChatIllegalArgumentException("Unknown relative channel '$channelName'.")
		return Pair(channel, messageContent)
	}

	fun resolveSubChannelForName(name: String) = map[name]

	override fun toString() = name
}
