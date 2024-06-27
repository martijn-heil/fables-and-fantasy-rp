/*
 * Fables and Fantasy RP kotlin plugins.
 * Copyright (C) 2024  Martijn Heil
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
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
