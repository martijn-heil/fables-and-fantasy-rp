/*
 * Fables and Fantasy RP kotlin plugins.
 * Copyright (C) 2024  Martijn Heil
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.fablesfantasyrp.plugin.charactermechanics.traits

import com.fablesfantasyrp.plugin.charactermechanics.traits.base.BaseTraitBehavior
import com.fablesfantasyrp.plugin.characters.domain.CharacterTrait
import com.fablesfantasyrp.plugin.characters.domain.repository.CharacterRepository
import com.fablesfantasyrp.plugin.profile.ProfileManager
import com.fablesfantasyrp.plugin.utils.every
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import org.bukkit.plugin.Plugin
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import kotlin.time.Duration.Companion.milliseconds

// Players with this trait can move 6+d6 (agility) instead of 4+d6 (agility) -
// Outside CRP, players with this trait get a permanent speed 1 boost.
class Swift(plugin: Plugin,
			characters: CharacterRepository,
			profileManager: ProfileManager)
	: BaseTraitBehavior(CharacterTrait.SWIFT, plugin, characters, profileManager) {
	private val effect = PotionEffect(PotionEffectType.SPEED, 30, 0, false, false, true)

	override fun init() {
		super.init()

		every(plugin, 50.milliseconds) {
			getPlayersWithTrait().onEach { it.player.addPotionEffect(effect) }.collect()
		}
	}
}
