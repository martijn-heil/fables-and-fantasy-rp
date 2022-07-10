package com.fablesfantasyrp.plugin.chat.data.persistent.database

import com.fablesfantasyrp.plugin.chat.data.persistent.PersistentChatPlayerData
import com.fablesfantasyrp.plugin.chat.data.persistent.PersistentChatPlayerDataRepository
import org.bukkit.OfflinePlayer
import java.util.*

class DatabasePersistentChatPlayerDataRepository : PersistentChatPlayerDataRepository {
	override fun all(): Collection<PersistentChatPlayerData> {
		TODO("Not yet implemented")
	}

	override fun destroy(v: PersistentChatPlayerData) {
		TODO("Not yet implemented")
	}

	override fun create(v: PersistentChatPlayerData) {
		TODO("Not yet implemented")
	}

	override fun forOfflinePlayer(offlinePlayer: OfflinePlayer): PersistentChatPlayerData {
		TODO("Not yet implemented")
	}

	override fun forId(id: UUID): PersistentChatPlayerData? {
		TODO("Not yet implemented")
	}

	override fun allIds(): Collection<UUID> {
		TODO("Not yet implemented")
	}

	override fun update(v: PersistentChatPlayerData) {
		TODO("Not yet implemented")
	}
}
