package com.fablesfantasyrp.plugin.characters

import com.fablesfantasyrp.plugin.characters.dal.enums.CreatureSize
import com.fablesfantasyrp.plugin.characters.domain.KnownCharacterTraits
import com.fablesfantasyrp.plugin.characters.domain.entity.Character
import com.fablesfantasyrp.plugin.characters.domain.repository.CharacterTraitRepository

class CreatureSizeCalculatorImpl(private val traits: CharacterTraitRepository) : CreatureSizeCalculator {
	override fun getCreatureSize(character: Character): CreatureSize {
		return if (
			traits.hasTrait(character, KnownCharacterTraits.ABNORMALLY_TALL) ||
			traits.hasTrait(character, KnownCharacterTraits.HULKING_BRUTE)) {
			CreatureSize.LARGE
		} else {
			character.race.creatureSize
		}
	}
}
