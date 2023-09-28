package com.fablesfantasyrp.plugin.charactermechanics.traits

import com.fablesfantasyrp.plugin.charactermechanics.traits.base.BaseTraitBehaviour
import com.fablesfantasyrp.plugin.characters.domain.repository.CharacterRepository
import com.fablesfantasyrp.plugin.characters.domain.KnownCharacterTraits
import com.fablesfantasyrp.plugin.characters.domain.repository.CharacterTraitRepository
import com.fablesfantasyrp.plugin.profile.ProfileManager
import org.bukkit.plugin.Plugin
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

// (Resilience potion effect outside CRP)
class EnduringAsRock(plugin: Plugin,
					 characters: CharacterRepository,
					 profileManager: ProfileManager,
					 traits: CharacterTraitRepository)
	: BaseTraitBehaviour(KnownCharacterTraits.ENDURING_AS_ROCK, plugin, characters, profileManager, traits) {
	private val effect = PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 30, 0, false, false, true)

	override fun init() {
		super.init()

		server.scheduler.scheduleSyncRepeatingTask(plugin, {
			getPlayersWithTrait().forEach { it.player.addPotionEffect(effect) }
		}, 0, 1)
	}
}
