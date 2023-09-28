package com.fablesfantasyrp.plugin.characters.event

import com.fablesfantasyrp.plugin.characters.domain.CharacterStats
import com.fablesfantasyrp.plugin.characters.domain.entity.Character
import org.bukkit.event.Event
import org.bukkit.event.HandlerList

class CharacterChangeStatsEvent(val character: Character, val old: CharacterStats, val new: CharacterStats) : Event() {
	override fun getHandlers(): HandlerList = Companion.handlers

	companion object {
		private val handlers = HandlerList()

		@JvmStatic
		fun getHandlerList() = handlers
	}
}
