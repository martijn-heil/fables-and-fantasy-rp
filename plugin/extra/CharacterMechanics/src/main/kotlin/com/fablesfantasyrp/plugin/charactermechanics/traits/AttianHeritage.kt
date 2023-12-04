package com.fablesfantasyrp.plugin.charactermechanics.traits

import com.fablesfantasyrp.plugin.charactermechanics.traits.base.BaseTraitBehavior
import com.fablesfantasyrp.plugin.characters.domain.CharacterTrait
import com.fablesfantasyrp.plugin.characters.domain.entity.Character
import com.fablesfantasyrp.plugin.characters.domain.repository.CharacterRepository
import com.fablesfantasyrp.plugin.characters.modifiers.health.HealthModifier
import com.fablesfantasyrp.plugin.profile.ProfileManager
import org.bukkit.plugin.Plugin

// Players with this trait get an additional 2 HP.
class AttianHeritage(plugin: Plugin,
					 characters: CharacterRepository,
					 profileManager: ProfileManager)
	: BaseTraitBehavior(CharacterTrait.ATTIAN_HERITAGE, plugin, characters, profileManager), HealthModifier {

	override fun calculateModifier(who: Character): Int = if (who.traits.contains(trait)) 2 else 0
}
