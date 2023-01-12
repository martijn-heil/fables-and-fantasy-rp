package com.fablesfantasyrp.plugin.playerinstance.event

import com.fablesfantasyrp.plugin.playerinstance.data.entity.PlayerInstance
import org.bukkit.entity.Player
import org.bukkit.event.Event
import org.bukkit.event.HandlerList

class PostPlayerSwitchPlayerInstanceEvent(val player: Player, val old: PlayerInstance?, val new: PlayerInstance?) : Event() {
	override fun getHandlers(): HandlerList = Companion.handlers

	companion object {
		private val handlers = HandlerList()

		@JvmStatic
		fun getHandlerList() = handlers
	}
}
