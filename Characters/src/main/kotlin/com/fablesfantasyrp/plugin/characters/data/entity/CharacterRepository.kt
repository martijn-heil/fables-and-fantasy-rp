package com.fablesfantasyrp.plugin.characters.data.entity

import com.fablesfantasyrp.plugin.database.repository.KeyedRepository
import com.fablesfantasyrp.plugin.database.repository.MutableRepository
import com.fablesfantasyrp.plugin.profile.data.entity.Profile
import org.bukkit.OfflinePlayer

interface CharacterRepository : MutableRepository<Character>, KeyedRepository<Int, Character>  {
	fun forOwner(offlinePlayer: OfflinePlayer?): Collection<Character>
	fun activeForOwner(offlinePlayer: OfflinePlayer?): Collection<Character> = this.forOwner(offlinePlayer).filter { it.profile.isActive }
	fun forProfile(profile: Profile): Character?
	fun forName(name: String): Character?
	fun nameExists(name: String): Boolean
	fun allNames(): Set<String> = nameMap.keys
	val nameMap: Map<String, Int>
}
