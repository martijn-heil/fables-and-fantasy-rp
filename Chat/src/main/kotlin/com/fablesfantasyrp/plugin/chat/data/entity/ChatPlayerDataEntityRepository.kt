package com.fablesfantasyrp.plugin.chat.data.entity

import com.fablesfantasyrp.plugin.database.OnlinePlayerCacheMarker
import com.fablesfantasyrp.plugin.database.entity.SimpleEntityRepository
import com.fablesfantasyrp.plugin.database.repository.HasDirtyMarker
import com.fablesfantasyrp.plugin.database.repository.KeyedRepository
import com.fablesfantasyrp.plugin.database.repository.MutableRepository
import org.bukkit.plugin.Plugin
import java.util.*

class ChatPlayerDataEntityRepository<C>(private val plugin: Plugin, child: C) : SimpleEntityRepository<UUID, ChatPlayerEntity, C>(child)
	where C: KeyedRepository<UUID, ChatPlayerEntity>,
		  C: MutableRepository<ChatPlayerEntity>,
		  C: HasDirtyMarker<ChatPlayerEntity> {
	init {
		OnlinePlayerCacheMarker(plugin, this) { p -> forId(p.uniqueId)!! }.start()
	}
}
