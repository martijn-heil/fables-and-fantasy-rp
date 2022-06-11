package com.fablesfantasyrp.plugin.form

import com.fablesfantasyrp.plugin.playerdata.FablesPlayer
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CompletableDeferred
import net.kyori.adventure.text.Component
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerChatEvent
import org.bukkit.event.player.PlayerQuitEvent


suspend fun promptChat(p: FablesPlayer, query: Component): String {
	val deferred = CompletableDeferred<String>()
	p.currentChatInputForm = deferred
	p.player.sendMessage(query)
	val result = deferred.await()
	p.currentChatInputForm = null
	return result
}

suspend fun promptChat(p: FablesPlayer, query: String): String {
	return promptChat(p, Component.text(query))
}

class ChatInputListener : Listener {
	@EventHandler(priority = EventPriority.LOWEST)
	fun onPlayerChat(e: PlayerChatEvent) {
		val fPlayer = FablesPlayer.forPlayer(e.player)
		val form = fPlayer.currentChatInputForm ?: return
		e.isCancelled = true
		if (!form.isCompleted) form.complete(e.message)
	}

	@EventHandler(priority = EventPriority.LOWEST)
	fun onPlayerQuit(e: PlayerQuitEvent) {
		val fPlayer = FablesPlayer.forPlayer(e.player)
		fPlayer.currentChatInputForm?.cancel(CancellationException("Player left the game"))
		fPlayer.currentChatInputForm = null
	}
}
