package com.fablesfantasyrp.plugin.form

import com.fablesfantasyrp.plugin.gui.ResultProducingGui
import kotlinx.coroutines.CompletableDeferred
import net.kyori.adventure.text.Component
import org.bukkit.entity.Player

private val currentChatWaitMap = HashMap<Player, CompletableDeferred<Unit>>()
private val currentChatInputFormMap = HashMap<Player, CompletableDeferred<String>>()

var Player.currentChatInputForm: CompletableDeferred<String>?
	get() = currentChatInputFormMap[this]
	set(value) {
		if (value != null) {
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

suspend fun Player.promptChat(query: String) = promptChat(this, query)
suspend fun Player.promptChat(query: Component) = promptChat(this, query)

suspend fun<T> Player.promptGui(gui: ResultProducingGui<T>) = promptGui(this, gui)
