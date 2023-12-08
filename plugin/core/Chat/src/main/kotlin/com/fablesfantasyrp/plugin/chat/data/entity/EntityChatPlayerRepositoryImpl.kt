package com.fablesfantasyrp.plugin.chat.data.entity

import com.fablesfantasyrp.plugin.database.OnlinePlayerCacheMarker
import com.fablesfantasyrp.plugin.database.entity.SimpleEntityRepository
import com.fablesfantasyrp.plugin.database.model.HasDirtyMarker
import com.fablesfantasyrp.plugin.database.sync.repository.KeyedRepository
import com.fablesfantasyrp.plugin.database.sync.repository.MutableRepository
import org.bukkit.OfflinePlayer
import org.bukkit.plugin.Plugin
import java.util.*

class EntityChatPlayerRepositoryImpl<C>(private val plugin: Plugin, child: C) : SimpleEntityRepository<UUID, ChatPlayer, C>(child),
		EntityChatPlayerRepository
	where C: KeyedRepository<UUID, ChatPlayer>,
		  C: MutableRepository<ChatPlayer>,
		  C: HasDirtyMarker<ChatPlayer> {
	init {
		OnlinePlayerCacheMarker(plugin, this) { p -> forId(p.uniqueId)!! }.start()
	}

	override fun forOfflinePlayer(offlinePlayer: OfflinePlayer): ChatPlayer
		= forId(offlinePlayer.uniqueId)!!
}
