package com.fablesfantasyrp.plugin.chat.channel

import org.bukkit.entity.Player

interface ChatChannel {
	fun getRecipients(from: Player): Sequence<Player>

	@Throws(ChatIllegalArgumentException::class)
	fun sendMessage(from: Player, message: String)

	companion object
}
