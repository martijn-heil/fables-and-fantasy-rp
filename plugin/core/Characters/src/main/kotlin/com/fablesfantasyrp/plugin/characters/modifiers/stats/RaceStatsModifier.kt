package com.fablesfantasyrp.plugin.characters.modifiers.stats

import com.fablesfantasyrp.plugin.characters.domain.CharacterStatsModifier
import com.fablesfantasyrp.plugin.characters.domain.entity.Character

class RaceStatsModifier : StatsModifier {
	override fun calculateModifiers(who: Character): CharacterStatsModifier = who.race.boosters
}
