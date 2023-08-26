package com.fablesfantasyrp.plugin.charactermechanics.traits

import com.fablesfantasyrp.plugin.charactermechanics.traits.base.BaseTraitBehaviour
import com.fablesfantasyrp.plugin.characters.data.entity.EntityCharacterRepository
import com.fablesfantasyrp.plugin.charactertraits.domain.KnownCharacterTraits
import com.fablesfantasyrp.plugin.charactertraits.domain.repository.CharacterTraitRepository
import com.fablesfantasyrp.plugin.profile.ProfileManager
import org.bukkit.plugin.Plugin
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

// Players with this trait can move 6+d6 (agility) instead of 4+d6 (agility) -
// Outside CRP, players with this trait get a permanent speed 1 boost. (Speed 2 on roads)
class Swift(plugin: Plugin,
			characters: EntityCharacterRepository,
			profileManager: ProfileManager,
			traits: CharacterTraitRepository)
	: BaseTraitBehaviour(KnownCharacterTraits.NIGHTSEER, plugin, characters, profileManager, traits) {
	private val effect = PotionEffect(PotionEffectType.SPEED, 30, 1, false, false, true)

	override fun init() {
		super.init()

		server.scheduler.scheduleSyncRepeatingTask(plugin, {
			getPlayersWithTrait().forEach { it.player.addPotionEffect(effect) }
		}, 0, 1)
	}
}
