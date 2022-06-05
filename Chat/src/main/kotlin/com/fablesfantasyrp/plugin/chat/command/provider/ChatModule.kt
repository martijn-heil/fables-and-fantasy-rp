package com.fablesfantasyrp.plugin.chat.command.provider

import com.fablesfantasyrp.plugin.chat.ChatChannel
import com.fablesfantasyrp.plugin.chat.ToggleableChatChannel
import com.sk89q.intake.parametric.AbstractModule
import org.bukkit.Server

class ChatModule(private val server: Server) : AbstractModule() {
	override fun configure() {
		bind(ToggleableChatChannel::class.java).toProvider(ToggleableChatChannelProvider(server))
		bind(ChatChannel::class.java).toProvider(ChatChannelProvider(server))
	}
}
