package com.fablesfantasyrp.plugin.form

import com.fablesfantasyrp.plugin.gui.ResultProducingGui
import com.fablesfantasyrp.plugin.playerdata.FablesOfflinePlayer
import com.fablesfantasyrp.plugin.playerdata.FablesPlayer
import kotlinx.coroutines.CompletableDeferred
import net.kyori.adventure.text.Component

private val currentChatInputFormMap = HashMap<FablesOfflinePlayer, CompletableDeferred<String>>()

var FablesOfflinePlayer.currentChatInputForm: CompletableDeferred<String>?
	get() = currentChatInputFormMap[this]
	set(value) {
		if (value != null) {
			currentChatInputFormMap[this] = value
		} else {
			currentChatInputFormMap.remove(this)
		}
	}

suspend fun FablesPlayer.promptChat(query: String) = promptChat(this, query)
suspend fun FablesPlayer.promptChat(query: Component) = promptChat(this, query)

suspend fun<T> FablesPlayer.promptGui(gui: ResultProducingGui<T>) = promptGui(this, gui)
