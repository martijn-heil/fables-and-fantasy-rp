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
package com.fablesfantasyrp.plugin.characters.domain.mapper

import com.fablesfantasyrp.plugin.characters.dal.model.CharacterData
import com.fablesfantasyrp.plugin.characters.dal.repository.CharacterDataRepository
import com.fablesfantasyrp.plugin.characters.domain.entity.Character
import com.fablesfantasyrp.plugin.database.CacheMarker
import com.fablesfantasyrp.plugin.database.async.repository.base.AsyncMappingRepository
import com.fablesfantasyrp.plugin.database.model.HasCacheMarker
import com.fablesfantasyrp.plugin.database.model.HasDirtyMarker
import com.fablesfantasyrp.plugin.database.repository.DirtyMarker
import com.fablesfantasyrp.plugin.profile.data.entity.Profile
import com.fablesfantasyrp.plugin.profile.data.entity.ProfileRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.bukkit.OfflinePlayer

class CharacterMapper(child: CharacterDataRepository, private val profiles: ProfileRepository)
	: AsyncMappingRepository<Int, CharacterData, Character, CharacterDataRepository>(child),
	HasDirtyMarker<Character>, HasCacheMarker<Character> {

	override var dirtyMarker: DirtyMarker<Character>? = null
	override var cacheMarker: CacheMarker<Character>? = null

	override fun convertFromChild(v: CharacterData) = Character(
		id = v.id,
		profile = profiles.forId(v.id)!!,
		name = v.name,
		description = v.description,
		stats = v.stats,
		race = v.race,
		gender = v.gender,
		dateOfBirth = v.dateOfBirth,
		dateOfNaturalDeath = v.dateOfNaturalDeath,
		lastSeen = v.lastSeen,
		createdAt = v.createdAt,
		isDead = v.isDead,
		diedAt = v.diedAt,
		isShelved = v.isShelved,
		shelvedAt = v.shelvedAt,
		changedStatsAt = v.changedStatsAt,
		traits = v.traits,
		dirtyMarker = dirtyMarker
	)

	override fun convertToChild(v: Character) = CharacterData(
		id = v.id,
		name = v.name,
		description = v.description,
		stats = v.stats,
		race = v.race,
		gender = v.gender,
		dateOfBirth = v.dateOfBirth,
		dateOfNaturalDeath = v.dateOfNaturalDeath,
		lastSeen = v.lastSeen,
		createdAt = v.createdAt,
		isDead = v.isDead,
		diedAt = v.diedAt,
		isShelved = v.isShelved,
		shelvedAt = v.shelvedAt,
		changedStatsAt = v.changedStatsAt,
		traits = v.traits
	)

	suspend fun forOwner(offlinePlayer: OfflinePlayer?): Collection<Character>
		= withContext(Dispatchers.IO) { child.forOwner(offlinePlayer) }.map { convertFromChild(it) }

	suspend fun forProfile(profile: Profile): Character?
		= withContext(Dispatchers.IO) { child.forProfile(profile) }?.let { convertFromChild(it) }

	suspend fun forName(name: String): Character?
		= withContext(Dispatchers.IO) { child.forName(name) }?.let { convertFromChild(it) }

	suspend fun nameExists(name: String): Boolean = withContext(Dispatchers.IO) { child.nameExists(name) }

	suspend fun nameMap(): Map<String, Int> = withContext(Dispatchers.IO) { child.nameMap }
}
