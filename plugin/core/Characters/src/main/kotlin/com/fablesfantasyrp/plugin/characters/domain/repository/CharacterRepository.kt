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
package com.fablesfantasyrp.plugin.characters.domain.repository

import com.fablesfantasyrp.plugin.characters.domain.entity.Character
import com.fablesfantasyrp.plugin.database.async.repository.AsyncKeyedRepository
import com.fablesfantasyrp.plugin.database.async.repository.AsyncMutableRepository
import com.fablesfantasyrp.plugin.profile.data.entity.Profile
import org.bukkit.OfflinePlayer

interface CharacterRepository : AsyncMutableRepository<Character>, AsyncKeyedRepository<Int, Character> {
	suspend fun forOwner(offlinePlayer: OfflinePlayer?): Collection<Character>
	suspend fun activeForOwner(offlinePlayer: OfflinePlayer?): Collection<Character> = this.forOwner(offlinePlayer).filter { it.profile.isActive }
	suspend fun forProfile(profile: Profile): Character?
	suspend fun forName(name: String): Character?
	suspend fun nameExists(name: String): Boolean
	suspend fun allNames(): Set<String> = nameMap.keys
	val nameMap: Map<String, Int>
}
