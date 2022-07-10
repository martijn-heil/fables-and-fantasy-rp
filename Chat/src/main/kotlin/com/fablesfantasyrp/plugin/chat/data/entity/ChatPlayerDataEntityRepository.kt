package com.fablesfantasyrp.plugin.chat.data.entity

import com.fablesfantasyrp.plugin.chat.data.ChatPlayerData
import com.fablesfantasyrp.plugin.database.OnlinePlayerCacheMarker
import com.fablesfantasyrp.plugin.database.entity.SimpleEntityRepository
import com.fablesfantasyrp.plugin.database.repository.HasDirtyMarker
import com.fablesfantasyrp.plugin.database.repository.KeyedRepository
import com.fablesfantasyrp.plugin.database.repository.MutableRepository
import org.bukkit.plugin.Plugin
import java.util.*

class ChatPlayerDataEntityRepository<C>(private val plugin: Plugin, child: C) : SimpleEntityRepository<UUID, ChatPlayerData, C>(child)
	where C: KeyedRepository<UUID, ChatPlayerData>,
		  C: MutableRepository<ChatPlayerData>,
		  C: HasDirtyMarker<ChatPlayerData> {
	init {
		OnlinePlayerCacheMarker(plugin, this) { p -> forId(p.uniqueId)!! }.start()
	}
}
