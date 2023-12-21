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
