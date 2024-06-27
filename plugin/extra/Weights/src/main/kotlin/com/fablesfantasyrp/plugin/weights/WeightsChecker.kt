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
package com.fablesfantasyrp.plugin.weights

import com.fablesfantasyrp.plugin.characters.domain.CharacterTrait
import com.fablesfantasyrp.plugin.characters.domain.repository.CharacterRepository
import com.fablesfantasyrp.plugin.profile.ProfileManager
import com.fablesfantasyrp.plugin.utils.every
import com.github.shynixn.mccoroutine.bukkit.launch
import kotlinx.coroutines.Job
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin
import java.time.Duration
import kotlin.time.toKotlinDuration

class WeightsChecker(private val plugin: Plugin,
					 private val config: WeightsConfig,
					 private val profileManager: ProfileManager,
					 private val characters: CharacterRepository) {
	private val server = plugin.server
	private lateinit var job: Job

	fun start() {
		job = every(plugin, Duration.ofMillis(50).toKotlinDuration()) {
			for (player in server.onlinePlayers) {

				val items = player.inventory.contents.filterNotNull()
				val weight = calculateWeight(items, config)

				flaunch { applyWeight(player, weight, calculateCap(player)) }
			}
		}
	}

	fun stop() {
		job.cancel()
	}

	private suspend fun calculateCap(player: Player): Int {
		val character = profileManager.getCurrentForPlayer(player)?.let { characters.forProfile(it) }
		return config.cap + if (character != null && character.traits.contains(CharacterTrait.PACKMULE)) 20 else 0
	}
}
