package com.fablesfantasyrp.plugin.playerinstance.data.entity

import com.fablesfantasyrp.plugin.database.repository.KeyedRepository
import com.fablesfantasyrp.plugin.database.repository.MutableRepository
import com.fablesfantasyrp.plugin.database.repository.Repository
import org.bukkit.OfflinePlayer

interface PlayerInstanceRepository :
		Repository<PlayerInstance>,
		MutableRepository<PlayerInstance>,
		KeyedRepository<Int, PlayerInstance> {
			fun forOwner(offlinePlayer: OfflinePlayer): Collection<PlayerInstance>
}
