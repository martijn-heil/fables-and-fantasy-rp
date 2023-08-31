package com.fablesfantasyrp.plugin.characters

import com.fablesfantasyrp.plugin.characters.data.CreatureSize
import com.fablesfantasyrp.plugin.characters.data.entity.Character

interface CreatureSizeCalculator {
	fun getCreatureSize(character: Character): CreatureSize
}
