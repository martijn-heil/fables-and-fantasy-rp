package com.fablesfantasyrp.plugin.knockout.data.persistent

import com.fablesfantasyrp.plugin.database.sync.repository.KeyedRepository
import com.fablesfantasyrp.plugin.database.sync.repository.MutableRepository
import org.bukkit.OfflinePlayer
import java.util.*

interface PersistentKnockoutPlayerDataRepository :
		KeyedRepository<UUID, PersistentKnockoutPlayerData>,
	MutableRepository<PersistentKnockoutPlayerData> {
	fun forOfflinePlayer(offlinePlayer: OfflinePlayer): PersistentKnockoutPlayerData
}
