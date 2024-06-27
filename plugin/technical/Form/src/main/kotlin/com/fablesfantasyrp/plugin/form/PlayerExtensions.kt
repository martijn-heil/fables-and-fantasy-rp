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
