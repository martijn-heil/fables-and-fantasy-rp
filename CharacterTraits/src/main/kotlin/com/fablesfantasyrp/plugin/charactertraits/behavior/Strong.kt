package com.fablesfantasyrp.plugin.charactertraits.behavior

import com.fablesfantasyrp.plugin.characters.StatsModifier
import com.fablesfantasyrp.plugin.characters.data.CharacterStatsModifier
import com.fablesfantasyrp.plugin.characters.data.entity.Character
import com.fablesfantasyrp.plugin.characters.data.entity.EntityCharacterRepository
import com.fablesfantasyrp.plugin.charactertraits.behavior.base.BaseTraitBehaviour
import com.fablesfantasyrp.plugin.charactertraits.domain.KnownCharacterTraits
import com.fablesfantasyrp.plugin.charactertraits.domain.repository.CharacterTraitRepository
import com.fablesfantasyrp.plugin.profile.ProfileManager
import org.bukkit.plugin.Plugin

// Players with this trait can move 6+d6 (agility) instead of 4+d6 (agility) -
// Outside CRP, players with this trait get a permanent speed 1 boost. (Speed 2 on roads)
class Strong(plugin: Plugin,
			 characters: EntityCharacterRepository,
			 profileManager: ProfileManager,
			 traits: CharacterTraitRepository)
	: BaseTraitBehaviour(KnownCharacterTraits.STRONG, plugin, characters, profileManager, traits), StatsModifier {

	override fun calculateModifiers(who: Character): CharacterStatsModifier
		= if (traits.hasTrait(who, trait)) CharacterStatsModifier(strength = 2) else CharacterStatsModifier()
}
