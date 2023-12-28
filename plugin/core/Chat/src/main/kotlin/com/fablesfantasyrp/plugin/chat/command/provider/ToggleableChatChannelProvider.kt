package com.fablesfantasyrp.plugin.chat.command.provider

import com.fablesfantasyrp.plugin.chat.channel.ChatChannel
import com.fablesfantasyrp.plugin.chat.channel.ToggleableChatChannel
import com.fablesfantasyrp.plugin.chat.channel.allStatic
import com.fablesfantasyrp.plugin.chat.channel.fromString
import com.fablesfantasyrp.caturix.argument.ArgumentParseException
import com.fablesfantasyrp.caturix.argument.CommandArgs
import com.fablesfantasyrp.caturix.argument.Namespace
import com.fablesfantasyrp.caturix.parametric.Provider
import org.bukkit.Server

class ToggleableChatChannelProvider(private val server: Server) : Provider<ToggleableChatChannel> {
	override val isProvided: Boolean = false

	override fun get(arguments: CommandArgs, modifiers: List<Annotation>): ToggleableChatChannel {
		val channelName = arguments.next().lowercase()
		val channel = ChatChannel.fromString(channelName)
				?: throw ArgumentParseException("Chat channel '$channelName' not found.")

		return channel as? ToggleableChatChannel
				?: throw ArgumentParseException("Chat channel '$channelName' is not toggleable!")
	}

	override fun getSuggestions(prefix: String, locals: Namespace, modifiers: List<Annotation>): List<String> {
		return ChatChannel.allStatic().filterIsInstance(ToggleableChatChannel::class.java).map { it.toString() }
	}
}
