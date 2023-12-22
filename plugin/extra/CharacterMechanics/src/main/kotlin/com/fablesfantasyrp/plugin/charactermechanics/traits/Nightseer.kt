package com.fablesfantasyrp.plugin.charactermechanics.traits

import com.fablesfantasyrp.plugin.charactermechanics.traits.base.BaseTraitBehavior
import com.fablesfantasyrp.plugin.characters.domain.CharacterTrait
import com.fablesfantasyrp.plugin.characters.domain.repository.CharacterRepository
import com.fablesfantasyrp.plugin.profile.ProfileManager
import com.fablesfantasyrp.plugin.utils.every
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.onEach
import org.bukkit.plugin.Plugin
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import java.time.Duration
import kotlin.time.toKotlinDuration

// Players with this trait get the night vision effect when entering dark spaces and during the night.
class Nightseer(plugin: Plugin,
				characters: CharacterRepository,
				profileManager: ProfileManager)
	: BaseTraitBehavior(CharacterTrait.NIGHTSEER, plugin, characters, profileManager) {
	private val effect = PotionEffect(PotionEffectType.NIGHT_VISION, 600, 0, false, false, false)

	override fun init() {
		super.init()

		every(plugin, Duration.ofMillis(50).toKotlinDuration()) {
			getPlayersWithTrait()
				.filter { it.player.location.block.lightLevel <= 1.toByte() }
				.onEach { it.player.addPotionEffect(effect) }
		}

		server.scheduler.scheduleSyncRepeatingTask(plugin, {
		}, 0, 20)
	}
}
