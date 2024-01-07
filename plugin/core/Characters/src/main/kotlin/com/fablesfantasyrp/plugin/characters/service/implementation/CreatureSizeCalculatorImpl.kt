package com.fablesfantasyrp.plugin.characters.service.implementation

import com.fablesfantasyrp.plugin.characters.dal.enums.CreatureSize
import com.fablesfantasyrp.plugin.characters.domain.CharacterTrait
import com.fablesfantasyrp.plugin.characters.domain.entity.Character
import com.fablesfantasyrp.plugin.characters.service.api.CreatureSizeCalculator

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
