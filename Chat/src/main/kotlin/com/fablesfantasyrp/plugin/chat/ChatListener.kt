package com.fablesfantasyrp.plugin.chat

import com.fablesfantasyrp.plugin.chat.channel.ChatIllegalArgumentException
import com.fablesfantasyrp.plugin.chat.channel.ChatIllegalStateException
import com.fablesfantasyrp.plugin.chat.event.FablesChatEvent
import com.fablesfantasyrp.plugin.text.sendError
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Server
import org.bukkit.entity.Player
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

	@EventHandler(priority = HIGHEST, ignoreCancelled = true)
	fun onFablesChat(e: FablesChatEvent) {
		val player = e.sender as? Player ?: return
		val channel = e.channel
		val content = e.content

		val chatSpyMessage = "${ChatColor.GRAY}[$channel] ${player.name}: $content"
		Bukkit.getOnlinePlayers()
				.filter {
					if (it == player) return@filter false
					if (e.recipients.contains(it)) return@filter false
					if (!it.hasPermission(Permission.Command.ChatSpy)) return@filter false
					val data = it.chat
					data.hasPermissionForChannel(channel) && data.isChatSpyEnabled && !data.chatSpyExcludeChannels.contains(channel)
				}.forEach { it.sendMessage(chatSpyMessage) }
	}
}
