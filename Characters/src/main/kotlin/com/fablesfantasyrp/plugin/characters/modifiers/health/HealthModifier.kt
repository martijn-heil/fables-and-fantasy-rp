package com.fablesfantasyrp.plugin.characters.modifiers.health

import com.fablesfantasyrp.plugin.characters.data.entity.Character

interface HealthModifier {
	fun calculateModifier(who: Character): Int
}
