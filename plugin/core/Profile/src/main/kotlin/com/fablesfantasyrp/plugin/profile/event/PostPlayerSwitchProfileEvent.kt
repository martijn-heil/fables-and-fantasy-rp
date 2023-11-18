package com.fablesfantasyrp.plugin.profile.event

import com.fablesfantasyrp.plugin.profile.data.entity.Profile
import org.bukkit.entity.Player
import org.bukkit.event.Event
import org.bukkit.event.HandlerList

class PostPlayerSwitchProfileEvent(val player: Player, val old: Profile?, val new: Profile?) : Event() {
	override fun getHandlers(): HandlerList = Companion.handlers

	companion object {
		private val handlers = HandlerList()

		@JvmStatic
		fun getHandlerList() = handlers
	}
}
