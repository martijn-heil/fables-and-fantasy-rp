package com.fablesfantasyrp.plugin.characters.data

import org.bukkit.OfflinePlayer

interface PlayerCharacterRepository : CachingRepository<PlayerCharacter> {
	fun forId(id: ULong)
	fun all(): Collection<PlayerCharacter>
	fun allForPlayer(p: OfflinePlayer)
	fun save()
	fun destroy()
}
