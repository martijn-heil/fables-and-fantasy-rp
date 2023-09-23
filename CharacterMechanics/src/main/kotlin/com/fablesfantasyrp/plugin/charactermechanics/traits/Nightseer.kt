package com.fablesfantasyrp.plugin.charactermechanics.traits

import com.fablesfantasyrp.plugin.charactermechanics.traits.base.BaseTraitBehaviour
import com.fablesfantasyrp.plugin.characters.domain.repository.CharacterRepository
import com.fablesfantasyrp.plugin.characters.domain.KnownCharacterTraits
import com.fablesfantasyrp.plugin.characters.domain.repository.CharacterTraitRepository
import com.fablesfantasyrp.plugin.profile.ProfileManager
import org.bukkit.plugin.Plugin
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

// Players with this trait get the night vision effect when entering dark spaces and during the night.
class Nightseer(plugin: Plugin,
				characters: CharacterRepository,
				profileManager: ProfileManager,
				traits: CharacterTraitRepository)
	: BaseTraitBehaviour(KnownCharacterTraits.NIGHTSEER, plugin, characters, profileManager, traits) {
	private val effect = PotionEffect(PotionEffectType.NIGHT_VISION, 600, 0, false, false, false)

	override fun init() {
		super.init()

		server.scheduler.scheduleSyncRepeatingTask(plugin, {
			getPlayersWithTrait().filter { it.player.location.block.lightLevel <= 1.toByte() }
				.forEach { it.player.addPotionEffect(effect) }
		}, 0, 20)
	}
}
