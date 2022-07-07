package com.fablesfantasyrp.plugin.characters.data

import com.fablesfantasyrp.plugin.database.repository.Repository
import org.bukkit.OfflinePlayer

interface PlayerCharacterRepository : Repository<PlayerCharacter> {
	fun forId(id: ULong): PlayerCharacter
	fun allForPlayer(p: OfflinePlayer): Collection<PlayerCharacter>
}
