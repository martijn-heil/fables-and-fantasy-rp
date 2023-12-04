package com.fablesfantasyrp.plugin.charactermechanics.traits

import com.fablesfantasyrp.plugin.charactermechanics.traits.base.BaseTraitBehavior
import com.fablesfantasyrp.plugin.characters.domain.CharacterTrait
import com.fablesfantasyrp.plugin.characters.domain.repository.CharacterRepository
import com.fablesfantasyrp.plugin.profile.ProfileManager
import org.bukkit.plugin.Plugin
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

// (Resilience potion effect outside CRP)
class EnduringAsRock(plugin: Plugin,
					 characters: CharacterRepository,
					 profileManager: ProfileManager)
	: BaseTraitBehavior(CharacterTrait.ENDURING_AS_ROCK, plugin, characters, profileManager) {
	private val effect = PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 30, 0, false, false, true)

	override fun init() {
		super.init()

		server.scheduler.scheduleSyncRepeatingTask(plugin, {
			getPlayersWithTrait().forEach { it.player.addPotionEffect(effect) }
		}, 0, 1)
	}
}
