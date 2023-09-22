package com.fablesfantasyrp.plugin.characters.modifiers.health

import com.fablesfantasyrp.plugin.characters.domain.entity.Character

interface HealthModifier {
	fun calculateModifier(who: Character): Int
}
