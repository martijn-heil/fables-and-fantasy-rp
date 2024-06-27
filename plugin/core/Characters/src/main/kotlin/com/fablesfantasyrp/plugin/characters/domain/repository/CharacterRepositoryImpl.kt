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
import com.fablesfantasyrp.plugin.characters.domain.mapper.CharacterMapper
import com.fablesfantasyrp.plugin.characters.frunBlocking
import com.fablesfantasyrp.plugin.database.async.repository.base.AsyncTypicalRepository
import com.fablesfantasyrp.plugin.profile.data.entity.Profile
import com.fablesfantasyrp.plugin.profile.data.entity.ProfileRepository
import com.google.common.collect.BiMap
import com.google.common.collect.HashBiMap
import kotlinx.coroutines.sync.withLock
import org.bukkit.OfflinePlayer

class CharacterRepositoryImpl(child: CharacterMapper, private val profiles: ProfileRepository)
	: AsyncTypicalRepository<Int, Character, CharacterMapper>(child), CharacterRepository {

	override lateinit var nameMap: BiMap<String, Int>

	override fun init() {
		super.init()
		nameMap = HashBiMap.create()
		frunBlocking { child.nameMap().entries.forEach { nameMap[it.key] = it.value } }
	}

	override suspend fun create(v: Character): Character {
		val result = super.create(v)
		nameMap[result.name] = result.id
		return result
	}

	override suspend fun destroy(v: Character) {
		super.destroy(v)
		nameMap.remove(v.name)
	}

	override suspend fun forOwner(offlinePlayer: OfflinePlayer?): Collection<Character> {
		return profiles.allForOwner(offlinePlayer).mapNotNull { this.forId(it.id) }
	}

	override suspend fun forProfile(profile: Profile): Character? {
		return this.forId(profile.id)
	}

	override suspend fun forName(name: String): Character? {
		return nameMap[name]?.let { this.forId(it) }
	}

	override suspend fun nameExists(name: String): Boolean = nameMap.containsKey(name)

	override fun markDirty(v: Character) {
		nameMap.inverse()[v.id] = v.name
		super.markDirty(v)
	}
}
