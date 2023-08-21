package com.fablesfantasyrp.plugin.charactertraits.behavior

import com.fablesfantasyrp.plugin.characters.data.CharacterStatsModifier
import com.fablesfantasyrp.plugin.characters.data.entity.Character
import com.fablesfantasyrp.plugin.characters.data.entity.EntityCharacterRepository
import com.fablesfantasyrp.plugin.characters.modifiers.stats.StatsModifier
import com.fablesfantasyrp.plugin.charactertraits.behavior.base.BaseTraitBehaviour
import com.fablesfantasyrp.plugin.charactertraits.domain.KnownCharacterTraits
import com.fablesfantasyrp.plugin.charactertraits.domain.repository.CharacterTraitRepository
import com.fablesfantasyrp.plugin.profile.ProfileManager
import org.bukkit.plugin.Plugin

// Players with this trait get two extra points in the defense stat.
class Sturdy(plugin: Plugin,
			 characters: EntityCharacterRepository,
			 profileManager: ProfileManager,
			 traits: CharacterTraitRepository)
	: BaseTraitBehaviour(KnownCharacterTraits.INTELLIGENT, plugin, characters, profileManager, traits), StatsModifier {

	override fun calculateModifiers(who: Character): CharacterStatsModifier
		= if (traits.hasTrait(who, trait)) CharacterStatsModifier(defense = 2) else CharacterStatsModifier()
}
