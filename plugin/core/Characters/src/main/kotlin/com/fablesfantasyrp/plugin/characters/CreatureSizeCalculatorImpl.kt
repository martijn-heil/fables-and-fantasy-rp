package com.fablesfantasyrp.plugin.characters

import com.fablesfantasyrp.plugin.characters.dal.enums.CreatureSize
import com.fablesfantasyrp.plugin.characters.domain.CharacterTrait
import com.fablesfantasyrp.plugin.characters.domain.entity.Character

class CreatureSizeCalculatorImpl() : CreatureSizeCalculator {
	override fun getCreatureSize(character: Character): CreatureSize {
		return if (
			character.traits.contains(CharacterTrait.ABNORMALLY_TALL) ||
			character.traits.contains(CharacterTrait.HULKING_BRUTE)) {
			CreatureSize.LARGE
		} else {
			character.race.creatureSize
		}
	}
}
