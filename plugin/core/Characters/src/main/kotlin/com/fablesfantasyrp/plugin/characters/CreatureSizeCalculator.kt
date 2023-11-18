package com.fablesfantasyrp.plugin.characters

import com.fablesfantasyrp.plugin.characters.domain.entity.Character
import com.fablesfantasyrp.plugin.characters.dal.enums.CreatureSize

interface CreatureSizeCalculator {
	fun getCreatureSize(character: Character): CreatureSize
}
