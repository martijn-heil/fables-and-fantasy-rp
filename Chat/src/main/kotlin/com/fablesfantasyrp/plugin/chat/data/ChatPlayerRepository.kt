package com.fablesfantasyrp.plugin.chat.data

import com.fablesfantasyrp.plugin.chat.data.entity.ChatPlayer
import com.fablesfantasyrp.plugin.database.repository.KeyedRepository
import com.fablesfantasyrp.plugin.database.repository.MutableRepository
import org.bukkit.OfflinePlayer
import java.util.*

interface ChatPlayerRepository : MutableRepository<ChatPlayer>, KeyedRepository<UUID, ChatPlayer> {
	fun forOfflinePlayer(offlinePlayer: OfflinePlayer): ChatPlayer
}
