/*
 * Fables and Fantasy RP kotlin plugins.
 * Copyright (C) 2024  Martijn Heil
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
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
