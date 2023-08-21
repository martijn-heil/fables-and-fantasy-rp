package com.fablesfantasyrp.plugin.characters.modifiers.stats

import com.fablesfantasyrp.plugin.characters.data.CharacterStatsModifier
import com.fablesfantasyrp.plugin.characters.data.entity.Character

interface StatsModifier {
	fun calculateModifiers(who: Character): CharacterStatsModifier
}
