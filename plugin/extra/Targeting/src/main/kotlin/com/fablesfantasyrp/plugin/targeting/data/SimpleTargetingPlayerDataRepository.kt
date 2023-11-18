package com.fablesfantasyrp.plugin.targeting.data

import com.fablesfantasyrp.plugin.database.repository.KeyedRepository
import com.fablesfantasyrp.plugin.database.repository.MutableRepository
import org.bukkit.OfflinePlayer

interface SimpleTargetingPlayerDataRepository :
		MutableRepository<SimpleTargetingPlayerData>,
		KeyedRepository<OfflinePlayer, SimpleTargetingPlayerData> {
	fun forOfflinePlayer(offlinePlayer: OfflinePlayer) = this.forId(offlinePlayer)!!
}
