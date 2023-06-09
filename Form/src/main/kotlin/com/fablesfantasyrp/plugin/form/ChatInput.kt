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

data class ChatPrompt(val query: Component,
					  val getPreview: (String) -> Component = { Component.empty() },
					  private val result: CompletableDeferred<String> = CompletableDeferred())
	: CompletableDeferred<String> by result

suspend fun promptChat(p: Player, query: Component, getPreview: ((String) -> Component)? = null): String {
	return try {
		val prompt = ChatPrompt(query, getPreview ?: { Component.empty() })
		p.currentChatInputForm = prompt
		p.sendMessage(query)
		prompt.await()
	} finally {
		p.currentChatInputForm = null
	}
}

suspend fun promptChat(p: Player, query: String, getPreview: ((String) -> Component)? = null): String {
	return promptChat(p, Component.text(query), getPreview)
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
