package com.fablesfantasyrp.plugin.chat.command.provider

import com.fablesfantasyrp.plugin.chat.channel.ChatChannel
import com.fablesfantasyrp.plugin.chat.channel.ToggleableChatChannel
import com.fablesfantasyrp.plugin.chat.channel.allStatic
import com.fablesfantasyrp.plugin.chat.channel.fromString
import com.sk89q.intake.argument.ArgumentParseException
import com.sk89q.intake.argument.CommandArgs
import com.sk89q.intake.argument.Namespace
import com.sk89q.intake.parametric.Provider
import org.bukkit.Server

class ToggleableChatChannelProvider(private val server: Server) : Provider<ToggleableChatChannel> {
	override fun isProvided(): Boolean = false

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
