package com.fablesfantasyrp.plugin.staffmode.event

import org.bukkit.entity.Player
import org.bukkit.event.Event
import org.bukkit.event.HandlerList


class PlayerSwitchStaffDutyModeEvent(val player: Player, val goOnDuty: Boolean) : Event() {
	override fun getHandlers(): HandlerList = Companion.handlers

	companion object {
		private val handlers = HandlerList()

		@JvmStatic
		fun getHandlerList() = handlers
	}
}
