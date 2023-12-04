package com.fablesfantasyrp.plugin.charactermechanics.traits

import com.fablesfantasyrp.plugin.charactermechanics.traits.base.BaseTraitBehavior
import com.fablesfantasyrp.plugin.characters.domain.CharacterStatsModifier
import com.fablesfantasyrp.plugin.characters.domain.CharacterTrait
import com.fablesfantasyrp.plugin.characters.domain.entity.Character
import com.fablesfantasyrp.plugin.characters.domain.repository.CharacterRepository
import com.fablesfantasyrp.plugin.characters.modifiers.stats.StatsModifier
import com.fablesfantasyrp.plugin.profile.ProfileManager
import org.bukkit.plugin.Plugin

// Players with this trait get two extra points in the strength stat.
class Strong(plugin: Plugin,
			 characters: CharacterRepository,
			 profileManager: ProfileManager)
	: BaseTraitBehavior(CharacterTrait.STRONG, plugin, characters, profileManager), StatsModifier {

	override fun calculateModifiers(who: Character): CharacterStatsModifier
		= if (who.traits.contains(trait)) CharacterStatsModifier(strength = 2) else CharacterStatsModifier()
}
