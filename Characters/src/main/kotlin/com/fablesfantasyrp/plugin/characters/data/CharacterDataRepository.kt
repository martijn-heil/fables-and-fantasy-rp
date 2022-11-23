package com.fablesfantasyrp.plugin.characters.data

import com.fablesfantasyrp.plugin.database.repository.KeyedRepository
import com.fablesfantasyrp.plugin.database.repository.MutableRepository
import org.bukkit.OfflinePlayer

interface CharacterDataRepository :
		KeyedRepository<ULong, CharacterData>,
		MutableRepository<CharacterData> {
	fun forOfflinePlayer(offlinePlayer: OfflinePlayer): Collection<CharacterData>
}
