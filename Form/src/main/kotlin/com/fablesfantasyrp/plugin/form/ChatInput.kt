package com.fablesfantasyrp.plugin.form

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CompletableDeferred
import net.kyori.adventure.text.Component
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerChatEvent
import org.bukkit.event.player.PlayerQuitEvent


suspend fun promptChat(p: Player, query: Component): String {
	return try {
		val deferred = CompletableDeferred<String>()
		p.currentChatInputForm = deferred
		p.sendMessage(query)
		deferred.await()
	} finally {
		p.currentChatInputForm = null
	}
}

suspend fun promptChat(p: Player, query: String): String {
	return promptChat(p, Component.text(query))
}

class ChatInputListener : Listener {
	@EventHandler(priority = EventPriority.LOWEST)
	fun onPlayerChat(e: PlayerChatEvent) {
		val form = e.player.currentChatInputForm ?: return
		if (!form.isCompleted) {
			e.isCancelled = true
			form.complete(e.message)
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	fun onPlayerQuit(e: PlayerQuitEvent) {
		e.player.currentChatInputForm?.cancel(CancellationException("Player left the game"))
		e.player.currentChatInputForm = null
	}
}
