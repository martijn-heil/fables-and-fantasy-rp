package com.fablesfantasyrp.plugin.chat.data.persistent

import com.fablesfantasyrp.plugin.database.repository.KeyedRepository
import com.fablesfantasyrp.plugin.database.repository.MutableRepository
import org.bukkit.OfflinePlayer
import java.util.*

interface PersistentChatPlayerDataRepository :
		KeyedRepository<UUID, PersistentChatPlayerData>,
		MutableRepository<PersistentChatPlayerData> {
	fun forOfflinePlayer(offlinePlayer: OfflinePlayer): PersistentChatPlayerData
}
