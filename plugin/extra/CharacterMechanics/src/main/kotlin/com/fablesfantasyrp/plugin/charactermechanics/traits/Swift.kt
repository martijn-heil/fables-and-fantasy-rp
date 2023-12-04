package com.fablesfantasyrp.plugin.charactermechanics.traits

import com.fablesfantasyrp.plugin.charactermechanics.traits.base.BaseTraitBehavior
import com.fablesfantasyrp.plugin.characters.domain.CharacterTrait
import com.fablesfantasyrp.plugin.characters.domain.repository.CharacterRepository
import com.fablesfantasyrp.plugin.profile.ProfileManager
import org.bukkit.plugin.Plugin
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

// Players with this trait can move 6+d6 (agility) instead of 4+d6 (agility) -
// Outside CRP, players with this trait get a permanent speed 1 boost.
class Swift(plugin: Plugin,
			characters: CharacterRepository,
			profileManager: ProfileManager)
	: BaseTraitBehavior(CharacterTrait.SWIFT, plugin, characters, profileManager) {
	private val effect = PotionEffect(PotionEffectType.SPEED, 30, 0, false, false, true)

	override fun init() {
		super.init()

		server.scheduler.scheduleSyncRepeatingTask(plugin, {
			getPlayersWithTrait().forEach { it.player.addPotionEffect(effect) }
		}, 0, 1)
	}
}
