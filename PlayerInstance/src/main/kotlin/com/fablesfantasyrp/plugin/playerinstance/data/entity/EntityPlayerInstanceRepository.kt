package com.fablesfantasyrp.plugin.playerinstance.data.entity

import com.fablesfantasyrp.plugin.database.entity.EntityRepository
import org.bukkit.OfflinePlayer

interface EntityPlayerInstanceRepository : EntityRepository<Int, PlayerInstance>, PlayerInstanceRepository {
	override fun forOwner(offlinePlayer: OfflinePlayer): Collection<PlayerInstance>
}
