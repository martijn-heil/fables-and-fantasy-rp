package com.fablesfantasyrp.plugin.characters.domain.repository

import com.fablesfantasyrp.plugin.characters.domain.entity.Character
import com.fablesfantasyrp.plugin.characters.domain.mapper.CharacterMapper
import com.fablesfantasyrp.plugin.database.entity.EntityRepository
import com.fablesfantasyrp.plugin.database.entity.SimpleEntityRepository
import com.fablesfantasyrp.plugin.profile.data.entity.Profile
import com.fablesfantasyrp.plugin.profile.data.entity.ProfileRepository
import com.google.common.collect.BiMap
import com.google.common.collect.HashBiMap
import org.bukkit.OfflinePlayer
import kotlin.concurrent.withLock

class CharacterRepositoryImpl(child: CharacterMapper, private val profiles: ProfileRepository)
	: SimpleEntityRepository<Int, Character, CharacterMapper>(child), CharacterRepository, EntityRepository<Int, Character> {

	override lateinit var nameMap: BiMap<String, Int>

	override fun init() {
		super.init()
		nameMap = HashBiMap.create()
		child.nameMap.entries.forEach { nameMap[it.key] = it.value }
	}

	override fun create(v: Character): Character {
		val result = super.create(v)
		nameMap[result.name] = result.id
		return result
	}

	override fun destroy(v: Character) {
		super.destroy(v)
		nameMap.remove(v.name)
	}

	override fun forOwner(offlinePlayer: OfflinePlayer?): Collection<Character> {
		return profiles.allForOwner(offlinePlayer).mapNotNull { this.forId(it.id) }
	}

	override fun forProfile(profile: Profile): Character? {
		return this.forId(profile.id)
	}

	override fun forName(name: String): Character? {
		return nameMap[name]?.let { this.forId(it) }
	}

	override fun nameExists(name: String): Boolean = nameMap.containsKey(name)

	override fun markDirty(v: Character) {
		lock.writeLock().withLock {
			super<SimpleEntityRepository>.markDirty(v)
			nameMap.inverse()[v.id] = v.name
		}
	}
}
