package com.fablesfantasyrp.plugin.characters.service.api

import com.fablesfantasyrp.plugin.characters.dal.enums.CreatureSize
import com.fablesfantasyrp.plugin.characters.domain.entity.Character

interface CreatureSizeCalculator {
	fun getCreatureSize(character: Character): CreatureSize
}
