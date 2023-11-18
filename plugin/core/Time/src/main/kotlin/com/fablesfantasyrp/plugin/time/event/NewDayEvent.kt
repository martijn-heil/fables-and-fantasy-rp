package com.fablesfantasyrp.plugin.time.event

import com.fablesfantasyrp.plugin.time.javatime.FablesLocalDate
import org.bukkit.event.Event
import org.bukkit.event.HandlerList

class NewDayEvent(val yesterday: FablesLocalDate, val newDay: FablesLocalDate) : Event() {
	override fun getHandlers(): HandlerList = Companion.handlers

	companion object {
		private val handlers = HandlerList()

		@JvmStatic
		fun getHandlerList() = handlers
	}
}
