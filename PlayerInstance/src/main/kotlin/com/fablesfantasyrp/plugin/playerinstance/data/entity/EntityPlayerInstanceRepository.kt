package com.fablesfantasyrp.plugin.playerinstance.data.entity

import com.fablesfantasyrp.plugin.database.entity.MassivelyCachingEntityRepository
import com.fablesfantasyrp.plugin.database.repository.HasDirtyMarker
import com.fablesfantasyrp.plugin.database.repository.KeyedRepository
import com.fablesfantasyrp.plugin.database.repository.MutableRepository
import org.bukkit.OfflinePlayer

class EntityPlayerInstanceRepository<C>(private val child: C) : MassivelyCachingEntityRepository<Int, PlayerInstance, C>(child), PlayerInstanceRepository
		where C: KeyedRepository<Int, PlayerInstance>,
			  C: MutableRepository<PlayerInstance>,
			  C: HasDirtyMarker<PlayerInstance>,
			  C: PlayerInstanceRepository {
	override fun forOwner(offlinePlayer: OfflinePlayer): Collection<PlayerInstance> = child.forOwner(offlinePlayer)
}
