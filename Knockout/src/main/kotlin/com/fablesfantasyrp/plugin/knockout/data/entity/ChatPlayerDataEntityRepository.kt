package com.fablesfantasyrp.plugin.knockout.data.entity

import com.fablesfantasyrp.plugin.database.OnlinePlayerCacheMarker
import com.fablesfantasyrp.plugin.database.entity.SimpleEntityRepository
import com.fablesfantasyrp.plugin.database.repository.HasDirtyMarker
import com.fablesfantasyrp.plugin.database.repository.KeyedRepository
import com.fablesfantasyrp.plugin.database.repository.MutableRepository
import org.bukkit.plugin.Plugin
import java.util.*

class ChatPlayerDataEntityRepository<C>(private val plugin: Plugin, child: C) : SimpleEntityRepository<UUID, KnockoutPlayerEntity, C>(child)
	where C: KeyedRepository<UUID, KnockoutPlayerEntity>,
		  C: MutableRepository<KnockoutPlayerEntity>,
		  C: HasDirtyMarker<KnockoutPlayerEntity> {
	init {
		OnlinePlayerCacheMarker(plugin, this) { p -> forId(p.uniqueId)!! }.start()
	}
}
