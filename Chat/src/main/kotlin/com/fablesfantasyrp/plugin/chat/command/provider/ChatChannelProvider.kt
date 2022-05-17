package com.fablesfantasyrp.plugin.chat.command.provider

import com.fablesfantasyrp.plugin.chat.ChatChannel
import com.fablesfantasyrp.plugin.chat.fromString
import com.sk89q.intake.argument.ArgumentParseException
import com.sk89q.intake.argument.CommandArgs
import com.sk89q.intake.argument.Namespace
import com.sk89q.intake.parametric.Provider
import org.bukkit.Server

class ChatChannelProvider(private val server: Server) : Provider<ChatChannel> {
	override fun isProvided(): Boolean = false

	override fun get(arguments: CommandArgs, modifiers: List<Annotation>): ChatChannel {
		val channelName = arguments.next().lowercase()
		return ChatChannel.fromString(channelName) ?: throw ArgumentParseException("Chat channel '$channelName' not found.")
	}

	override fun getSuggestions(prefix: String, locals: Namespace, modifiers: List<Annotation>): List<String> {
		return listOf("ooc", "looc", "ic", "spectator", "staff")
	}
}
