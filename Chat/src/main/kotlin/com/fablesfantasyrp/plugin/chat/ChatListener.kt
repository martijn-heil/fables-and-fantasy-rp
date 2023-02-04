package com.fablesfantasyrp.plugin.chat

import com.fablesfantasyrp.plugin.chat.channel.ChatIllegalArgumentException
import com.fablesfantasyrp.plugin.chat.channel.ChatIllegalStateException
import com.fablesfantasyrp.plugin.text.sendError
import org.bukkit.Server
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority.HIGHEST
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerChatEvent

class ChatListener(private val server: Server) : Listener {
	@EventHandler(priority = HIGHEST, ignoreCancelled = true)
	fun onPlayerChat(e: PlayerChatEvent) {
		val player = e.player
		e.isCancelled = true
		if (e.message.isEmpty()) return
		try {
			player.chat.doChat(e.message)
		} catch (ex: ChatIllegalArgumentException) {
			player.sendError(ex.message ?: "Illegal argument.")
		} catch (ex: ChatIllegalStateException) {
			player.sendError(ex.message ?: "Illegal state.")
		}
	}
}
