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
package com.fablesfantasyrp.plugin.lodestones

import com.fablesfantasyrp.plugin.characters.domain.repository.CharacterRepository
import com.fablesfantasyrp.plugin.characters.isStaffCharacter
import com.fablesfantasyrp.plugin.lodestones.domain.entity.Lodestone
import com.fablesfantasyrp.plugin.lodestones.domain.repository.CharacterLodestoneRepository
import com.fablesfantasyrp.plugin.profile.ProfileManager
import com.fablesfantasyrp.plugin.profile.data.entity.Profile
import org.bukkit.entity.Player

class LodestoneAuthorizerImpl(private val characters: CharacterRepository,
							  private val profileManager: ProfileManager,
							  private val characterLodestones: CharacterLodestoneRepository) : LodestoneAuthorizer {
	override suspend fun mayWarpTo(who: Profile?, lodestone: Lodestone): Boolean {
		val character = who?.let { characters.forProfile(who) } ?: return true
		return lodestone.isPublic || character.isStaffCharacter || characterLodestones.forCharacter(character).contains(lodestone)
	}

	override suspend fun useCoolDown(who: Player): Boolean {
		val character = profileManager.getCurrentForPlayer(who)?.let { characters.forProfile(it) }
		return character != null && !character.isStaffCharacter
	}
}
