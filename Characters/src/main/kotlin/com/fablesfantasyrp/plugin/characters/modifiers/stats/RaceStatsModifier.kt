package com.fablesfantasyrp.plugin.characters.modifiers.stats

import com.fablesfantasyrp.plugin.characters.data.CharacterStatsModifier
import com.fablesfantasyrp.plugin.characters.data.entity.Character

class RaceStatsModifier : StatsModifier {
	override fun calculateModifiers(who: Character): CharacterStatsModifier = who.race.boosters
}
