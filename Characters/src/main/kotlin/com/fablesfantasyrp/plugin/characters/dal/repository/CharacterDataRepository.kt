package com.fablesfantasyrp.plugin.characters.dal.repository

import com.fablesfantasyrp.plugin.characters.dal.model.CharacterData
import com.fablesfantasyrp.plugin.database.repository.KeyedRepository
import com.fablesfantasyrp.plugin.database.repository.MutableRepository
import com.fablesfantasyrp.plugin.profile.data.entity.Profile
import org.bukkit.OfflinePlayer

interface CharacterDataRepository : MutableRepository<CharacterData>, KeyedRepository<Int, CharacterData>  {
	fun forOwner(offlinePlayer: OfflinePlayer?): Collection<CharacterData>
	fun forProfile(profile: Profile): CharacterData?
	fun forName(name: String): CharacterData?
	fun nameExists(name: String): Boolean
	fun allNames(): Set<String> = nameMap.keys
	val nameMap: Map<String, Int>
}
