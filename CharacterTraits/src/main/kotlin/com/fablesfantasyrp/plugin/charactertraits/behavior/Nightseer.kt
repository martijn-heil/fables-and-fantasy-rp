package com.fablesfantasyrp.plugin.charactertraits.behavior

import com.fablesfantasyrp.plugin.characters.data.entity.EntityCharacterRepository
import com.fablesfantasyrp.plugin.charactertraits.behavior.base.BaseTraitBehaviour
import com.fablesfantasyrp.plugin.charactertraits.domain.KnownCharacterTraits
import com.fablesfantasyrp.plugin.charactertraits.domain.repository.CharacterTraitRepository
import com.fablesfantasyrp.plugin.profile.ProfileManager
import org.bukkit.plugin.Plugin
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

// Players with this trait get the night vision effect when entering dark spaces and during the night.
class Nightseer(plugin: Plugin,
				characters: EntityCharacterRepository,
				profileManager: ProfileManager,
				traits: CharacterTraitRepository)
	: BaseTraitBehaviour(KnownCharacterTraits.NIGHTSEER, plugin, characters, profileManager, traits) {
	private val effect = PotionEffect(PotionEffectType.NIGHT_VISION, 30, 1, false, false, true)

	override fun init() {
		super.init()

		server.scheduler.scheduleSyncRepeatingTask(plugin, {
			getPlayersWithTrait().filter { it.player.location.block.lightLevel == 0.toByte() }
				.forEach { it.player.addPotionEffect(effect) }
		}, 0L, 20L)
	}
}
