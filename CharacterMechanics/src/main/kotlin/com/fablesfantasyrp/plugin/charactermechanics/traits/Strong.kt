package com.fablesfantasyrp.plugin.charactermechanics.traits

import com.fablesfantasyrp.plugin.charactermechanics.traits.base.BaseTraitBehaviour
import com.fablesfantasyrp.plugin.characters.domain.CharacterStatsModifier
import com.fablesfantasyrp.plugin.characters.domain.entity.Character
import com.fablesfantasyrp.plugin.characters.domain.repository.CharacterRepository
import com.fablesfantasyrp.plugin.characters.modifiers.stats.StatsModifier
import com.fablesfantasyrp.plugin.charactertraits.domain.KnownCharacterTraits
import com.fablesfantasyrp.plugin.charactertraits.domain.repository.CharacterTraitRepository
import com.fablesfantasyrp.plugin.profile.ProfileManager
import org.bukkit.plugin.Plugin

// Players with this trait get two extra points in the strength stat.
class Strong(plugin: Plugin,
			 characters: CharacterRepository,
			 profileManager: ProfileManager,
			 traits: CharacterTraitRepository)
	: BaseTraitBehaviour(KnownCharacterTraits.STRONG, plugin, characters, profileManager, traits), StatsModifier {

	override fun calculateModifiers(who: Character): CharacterStatsModifier
		= if (traits.hasTrait(who, trait)) CharacterStatsModifier(strength = 2) else CharacterStatsModifier()
}
