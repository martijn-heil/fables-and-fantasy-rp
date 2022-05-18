package com.fablesfantasyrp.plugin.chat

import com.fablesfantasyrp.plugin.playerdata.FablesPlayer
import org.bukkit.Server
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority.HIGHEST
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerChatEvent

class ChatListener(private val server: Server) : Listener {
	@EventHandler(priority = HIGHEST, ignoreCancelled = true)
	fun onPlayerChat(e: PlayerChatEvent) {
		e.isCancelled = true
		if (e.message.isEmpty()) return
		val fPlayer = FablesPlayer.forPlayer(e.player)
		try {
			fPlayer.doChat(e.message)
		} catch (e: IllegalArgumentException) {
			fPlayer.sendError(e.message ?: "IllegalArgumentException")
		}
	}
}
