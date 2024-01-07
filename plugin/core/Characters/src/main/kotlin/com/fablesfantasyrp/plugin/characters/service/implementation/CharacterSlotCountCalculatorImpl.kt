package com.fablesfantasyrp.plugin.characters.service.implementation

import com.fablesfantasyrp.plugin.characters.modifiers.characterslots.CharacterSlotCountModifier
import com.fablesfantasyrp.plugin.characters.service.api.CharacterSlotCountCalculator
import org.bukkit.entity.Player
import org.koin.core.context.GlobalContext

class CharacterSlotCountCalculatorImpl : CharacterSlotCountCalculator {
	override fun getCharacterSlots(player: Player): Int {
		val modifiers = GlobalContext.get().getAll<CharacterSlotCountModifier>()

		return 2 + modifiers.sumOf { it.calculateModifier(player) }
	}
}
