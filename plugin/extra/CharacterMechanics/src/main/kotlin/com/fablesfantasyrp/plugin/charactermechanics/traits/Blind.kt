package com.fablesfantasyrp.plugin.charactermechanics.traits

import com.fablesfantasyrp.plugin.charactermechanics.traits.base.BaseTraitBehavior
import com.fablesfantasyrp.plugin.characters.domain.CharacterStatsModifier
import com.fablesfantasyrp.plugin.characters.domain.CharacterTrait
import com.fablesfantasyrp.plugin.characters.domain.entity.Character
import com.fablesfantasyrp.plugin.characters.domain.repository.CharacterRepository
import com.fablesfantasyrp.plugin.characters.modifiers.stats.StatsModifier
import com.fablesfantasyrp.plugin.profile.ProfileManager
import com.fablesfantasyrp.plugin.utils.every
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import org.bukkit.plugin.Plugin
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import kotlin.time.Duration.Companion.milliseconds

class Blind(plugin: Plugin,
			characters: CharacterRepository,
			profileManager: ProfileManager)
	: BaseTraitBehavior(CharacterTrait.BLIND, plugin, characters, profileManager), StatsModifier {
	private val effect = PotionEffect(PotionEffectType.BLINDNESS, 30, 2, false, false, false)

	override fun init() {
		super.init()

		every(plugin, 50.milliseconds) {
			getPlayersWithTrait().onEach { it.player.addPotionEffect(effect) }.collect()
		}
	}

	override fun calculateModifiers(who: Character): CharacterStatsModifier {
		return if (who.traits.contains(CharacterTrait.BLIND)) {
			CharacterStatsModifier(
				strength = -2,
				defense = -2,
				agility = -2
			)
		} else CharacterStatsModifier()
	}
}
