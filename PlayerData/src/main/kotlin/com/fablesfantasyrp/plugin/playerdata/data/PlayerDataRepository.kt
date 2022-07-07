package com.fablesfantasyrp.plugin.playerdata.data

import com.fablesfantasyrp.plugin.database.repository.Repository
import org.bukkit.OfflinePlayer

interface PlayerDataRepository : Repository<PlayerData> {
	fun forOfflinePlayer(p: OfflinePlayer): PlayerData
	fun allOnline(): Collection<PlayerData>
}
