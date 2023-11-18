package com.fablesfantasyrp.plugin.chat.command.provider

import com.fablesfantasyrp.plugin.chat.channel.ChatChannel
import com.fablesfantasyrp.plugin.chat.channel.allNames
import com.fablesfantasyrp.plugin.chat.channel.fromStringAliased
import com.sk89q.intake.argument.ArgumentParseException
import com.sk89q.intake.argument.CommandArgs
import com.sk89q.intake.argument.Namespace
import com.sk89q.intake.parametric.Provider
import org.bukkit.Server
import org.bukkit.command.CommandSender

class ChatChannelProvider(private val server: Server) : Provider<ChatChannel> {
	override fun isProvided(): Boolean = false

	override fun get(arguments: CommandArgs, modifiers: List<Annotation>): ChatChannel {
		val sender = arguments.namespace.get("sender") as CommandSender
		val channelName = arguments.next().lowercase()
		return ChatChannel.fromStringAliased(channelName, sender) ?: throw ArgumentParseException("Chat channel '$channelName' not found.")
	}

	override fun getSuggestions(prefix: String, locals: Namespace, modifiers: List<Annotation>): List<String> {
		return ChatChannel.allNames().filter { it.startsWith(prefix) }.toList()
	}
}
