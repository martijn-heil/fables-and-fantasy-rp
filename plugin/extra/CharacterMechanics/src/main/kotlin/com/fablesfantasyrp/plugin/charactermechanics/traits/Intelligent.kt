package com.fablesfantasyrp.plugin.charactermechanics.traits

import com.fablesfantasyrp.plugin.charactermechanics.traits.base.BaseTraitBehavior
import com.fablesfantasyrp.plugin.characters.domain.CharacterStatsModifier
import com.fablesfantasyrp.plugin.characters.domain.CharacterTrait
import com.fablesfantasyrp.plugin.characters.domain.entity.Character
import com.fablesfantasyrp.plugin.characters.domain.repository.CharacterRepository
import com.fablesfantasyrp.plugin.characters.modifiers.stats.StatsModifier
import com.fablesfantasyrp.plugin.profile.ProfileManager
import org.bukkit.plugin.Plugin

// Players with this trait get two extra points in the intelligence stat.
class Intelligent(plugin: Plugin,
				  characters: CharacterRepository,
				  profileManager: ProfileManager)
	: BaseTraitBehavior(CharacterTrait.INTELLIGENT, plugin, characters, profileManager), StatsModifier {

	override fun calculateModifiers(who: Character): CharacterStatsModifier
		= if (who.traits.contains(trait)) CharacterStatsModifier(intelligence = 2) else CharacterStatsModifier()
}
