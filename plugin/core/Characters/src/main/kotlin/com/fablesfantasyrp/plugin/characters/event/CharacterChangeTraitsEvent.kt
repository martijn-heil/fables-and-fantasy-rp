package com.fablesfantasyrp.plugin.characters.event

import com.fablesfantasyrp.plugin.characters.domain.entity.Character
import com.fablesfantasyrp.plugin.characters.domain.entity.CharacterTrait
import org.bukkit.event.Event
import org.bukkit.event.HandlerList

class CharacterChangeTraitsEvent(val character: Character, val old: Set<CharacterTrait>, val new: Set<CharacterTrait>) : Event() {
	override fun getHandlers(): HandlerList = Companion.handlers

	companion object {
		private val handlers = HandlerList()

		@JvmStatic
		fun getHandlerList() = handlers
	}
}
