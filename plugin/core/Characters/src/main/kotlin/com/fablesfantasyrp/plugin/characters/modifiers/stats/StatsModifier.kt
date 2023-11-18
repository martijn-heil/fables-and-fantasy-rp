package com.fablesfantasyrp.plugin.characters.modifiers.stats

import com.fablesfantasyrp.plugin.characters.domain.CharacterStatsModifier
import com.fablesfantasyrp.plugin.characters.domain.entity.Character

interface StatsModifier {
	fun calculateModifiers(who: Character): CharacterStatsModifier
}
