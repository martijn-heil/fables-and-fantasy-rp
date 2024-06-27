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
package com.fablesfantasyrp.plugin.charactermechanics.traits.base

import com.fablesfantasyrp.plugin.characters.domain.CharacterTrait
import com.fablesfantasyrp.plugin.characters.domain.entity.Character
import com.fablesfantasyrp.plugin.characters.domain.repository.CharacterRepository
import com.fablesfantasyrp.plugin.profile.ProfileManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.mapNotNull
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin

abstract class BaseTraitBehavior(
	protected val trait: CharacterTrait,
	protected val plugin: Plugin,
	protected val characters: CharacterRepository,
	protected val profileManager: ProfileManager) : TraitBehavior {

	protected val server = plugin.server


	override fun init() {
	}

	protected suspend fun getPlayersWithTrait(trait: CharacterTrait = this.trait): Flow<ActiveTraitHolder> {
		return server.onlinePlayers.asFlow().mapNotNull {
			val profile = profileManager.getCurrentForPlayer(it) ?: return@mapNotNull null
			val character = characters.forProfile(profile) ?: return@mapNotNull null
			if (!character.traits.contains(trait)) return@mapNotNull null
			ActiveTraitHolder(it, character)
		}
	}

	protected suspend fun hasTrait(player: Player): Boolean {
		val character = profileManager.getCurrentForPlayer(player)?.let { characters.forProfile(it) } ?: return false
		return character.traits.contains(trait)
	}

	protected data class ActiveTraitHolder(val player: Player, val character: Character)
}
