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
package com.fablesfantasyrp.plugin.charactermechanics.racial.base

import com.fablesfantasyrp.plugin.characters.dal.enums.Race
import com.fablesfantasyrp.plugin.characters.domain.entity.Character
import com.fablesfantasyrp.plugin.characters.domain.repository.CharacterRepository
import com.fablesfantasyrp.plugin.profile.ProfileManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.mapNotNull
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin

abstract class BaseRaceBehavior(
	protected val race: Race,
	protected val plugin: Plugin,
	protected val characters: CharacterRepository,
	protected val profileManager: ProfileManager,
) : RaceBehavior {
	protected val server = plugin.server

	override fun init() {

	}

	protected suspend fun getPlayersWithRace(race: Race = this.race): Flow<ActiveRaceHolder> {
		return server.onlinePlayers.asFlow().mapNotNull {
			val profile = profileManager.getCurrentForPlayer(it) ?: return@mapNotNull null
			val character = characters.forProfile(profile) ?: return@mapNotNull null
			if (character.race != race) return@mapNotNull null
			ActiveRaceHolder(it, character)
		}
	}

	protected data class ActiveRaceHolder(val player: Player, val character: Character)
}
