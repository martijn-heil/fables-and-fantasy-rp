package com.fablesfantasyrp.plugin.form

import com.fablesfantasyrp.plugin.gui.ResultProducingInventoryGui
import kotlinx.coroutines.CompletableDeferred
import net.kyori.adventure.text.Component
import org.bukkit.entity.Player

private val currentChatWaitMap = HashMap<Player, CompletableDeferred<Unit>>()
private val currentChatInputFormMap = HashMap<Player, ChatPrompt>()

var Player.currentChatInputForm: ChatPrompt?
	get() = currentChatInputFormMap[this]
	set(value) {
		if (value != null) {
			val current = currentChatInputForm
			if (current != null && !current.isCancelled) current.cancel()
			currentChatInputFormMap[this] = value
		} else {
			currentChatInputFormMap.remove(this)
		}
	}

suspend fun Player.waitForChat() {
	val deferred = currentChatWaitMap.getOrPut(this) { CompletableDeferred() }
	deferred.await()
}

fun Player.completeWaitForChat() {
	currentChatWaitMap.remove(this)?.complete(Unit)
}

suspend fun Player.promptChat(query: String, getPreview: ((String) -> Component)? = null) = promptChat(this, query, getPreview)
suspend fun Player.promptChat(query: Component, getPreview: ((String) -> Component)? = null) = promptChat(this, query, getPreview)

suspend fun<T> Player.promptGui(gui: ResultProducingInventoryGui<T>) = promptGui(this, gui)
