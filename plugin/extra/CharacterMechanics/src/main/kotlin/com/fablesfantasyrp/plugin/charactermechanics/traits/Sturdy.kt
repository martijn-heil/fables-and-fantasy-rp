package com.fablesfantasyrp.plugin.charactermechanics.traits

import com.fablesfantasyrp.plugin.charactermechanics.traits.base.BaseTraitBehavior
import com.fablesfantasyrp.plugin.characters.domain.CharacterStatsModifier
import com.fablesfantasyrp.plugin.characters.domain.entity.Character
import com.fablesfantasyrp.plugin.characters.domain.repository.CharacterRepository
import com.fablesfantasyrp.plugin.characters.modifiers.stats.StatsModifier
import com.fablesfantasyrp.plugin.characters.domain.KnownCharacterTraits
import com.fablesfantasyrp.plugin.characters.domain.repository.CharacterTraitRepository
import com.fablesfantasyrp.plugin.profile.ProfileManager
import org.bukkit.plugin.Plugin

// Players with this trait get two extra points in the defense stat.
class Sturdy(plugin: Plugin,
			 characters: CharacterRepository,
			 profileManager: ProfileManager,
			 traits: CharacterTraitRepository)
	: BaseTraitBehavior(KnownCharacterTraits.STURDY, plugin, characters, profileManager, traits), StatsModifier {

	override fun calculateModifiers(who: Character): CharacterStatsModifier
		= if (traits.hasTrait(who, trait)) CharacterStatsModifier(defense = 2) else CharacterStatsModifier()
}
