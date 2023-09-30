package com.fablesfantasyrp.plugin.charactermechanics.traits

import com.fablesfantasyrp.plugin.charactermechanics.traits.base.BaseTraitBehavior
import com.fablesfantasyrp.plugin.characters.domain.KnownCharacterTraits
import com.fablesfantasyrp.plugin.characters.domain.entity.Character
import com.fablesfantasyrp.plugin.characters.domain.repository.CharacterRepository
import com.fablesfantasyrp.plugin.characters.domain.repository.CharacterTraitRepository
import com.fablesfantasyrp.plugin.characters.modifiers.health.HealthModifier
import com.fablesfantasyrp.plugin.profile.ProfileManager
import org.bukkit.plugin.Plugin

// Players with this trait get an additional 2 HP.
class AttianHeritage(plugin: Plugin,
					 characters: CharacterRepository,
					 profileManager: ProfileManager,
					 traits: CharacterTraitRepository)
	: BaseTraitBehavior(KnownCharacterTraits.ATTIAN_HERITAGE, plugin, characters, profileManager, traits), HealthModifier {

	override fun calculateModifier(who: Character): Int = if (traits.hasTrait(who, trait)) 2 else 0
}
