package com.fablesfantasyrp.plugin.characters.data.entity

import com.fablesfantasyrp.plugin.database.repository.KeyedRepository
import com.fablesfantasyrp.plugin.database.repository.MutableRepository
import org.bukkit.OfflinePlayer

interface CharacterRepository : MutableRepository<Character>, KeyedRepository<ULong, Character>  {
	fun forOwner(offlinePlayer: OfflinePlayer): Collection<Character>
}
