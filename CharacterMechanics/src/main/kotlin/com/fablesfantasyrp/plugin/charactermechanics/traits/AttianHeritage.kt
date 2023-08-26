package com.fablesfantasyrp.plugin.charactermechanics.traits

import com.fablesfantasyrp.plugin.charactermechanics.traits.base.BaseTraitBehaviour
import com.fablesfantasyrp.plugin.characters.data.entity.Character
import com.fablesfantasyrp.plugin.characters.data.entity.EntityCharacterRepository
import com.fablesfantasyrp.plugin.characters.modifiers.health.HealthModifier
import com.fablesfantasyrp.plugin.charactertraits.domain.KnownCharacterTraits
import com.fablesfantasyrp.plugin.charactertraits.domain.repository.CharacterTraitRepository
import com.fablesfantasyrp.plugin.profile.ProfileManager
import org.bukkit.plugin.Plugin

// Players with this trait get an additional 2 HP.
class AttianHeritage(plugin: Plugin,
					 characters: EntityCharacterRepository,
					 profileManager: ProfileManager,
					 traits: CharacterTraitRepository)
	: BaseTraitBehaviour(KnownCharacterTraits.ATTIAN_HERITAGE, plugin, characters, profileManager, traits), HealthModifier {

	override fun calculateModifier(who: Character): Int = if (traits.hasTrait(who, trait)) 2 else 0
}
