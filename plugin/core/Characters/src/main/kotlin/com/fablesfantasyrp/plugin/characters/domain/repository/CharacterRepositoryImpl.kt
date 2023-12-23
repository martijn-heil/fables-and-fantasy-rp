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
		child.nameMap.entries.forEach { nameMap[it.key] = it.value }
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
		frunBlocking {
			dirtyLock.withLock {
				nameMap.inverse()[v.id] = v.name
				super.markDirty(v)
			}
		}
	}
}
