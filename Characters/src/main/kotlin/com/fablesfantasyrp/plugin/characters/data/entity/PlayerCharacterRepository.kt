package com.fablesfantasyrp.plugin.characters.data.entity

import com.fablesfantasyrp.plugin.database.entity.EntityRepository
import org.bukkit.OfflinePlayer

interface PlayerCharacterRepository : EntityRepository<ULong, PlayerCharacter> {
	fun forOwner(offlinePlayer: OfflinePlayer)
}
