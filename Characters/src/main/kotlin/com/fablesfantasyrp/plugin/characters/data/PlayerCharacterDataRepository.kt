package com.fablesfantasyrp.plugin.characters.data

import com.fablesfantasyrp.plugin.database.repository.KeyedRepository
import com.fablesfantasyrp.plugin.database.repository.MutableRepository
import org.bukkit.OfflinePlayer

interface PlayerCharacterDataRepository :
		KeyedRepository<ULong, PlayerCharacterData>,
		MutableRepository<PlayerCharacterData> {
	fun forOfflinePlayer(offlinePlayer: OfflinePlayer): Collection<PlayerCharacterData>
}
