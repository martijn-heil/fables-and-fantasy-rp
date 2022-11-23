package com.fablesfantasyrp.plugin.location.data.entity

import com.fablesfantasyrp.plugin.database.entity.MassivelyCachingEntityRepository
import com.fablesfantasyrp.plugin.database.repository.HasDirtyMarker
import com.fablesfantasyrp.plugin.database.repository.KeyedRepository
import com.fablesfantasyrp.plugin.database.repository.MutableRepository
import com.fablesfantasyrp.plugin.playerinstance.data.entity.PlayerInstance

class EntityPlayerInstanceLocationRepository<C>(child: C)
	: MassivelyCachingEntityRepository<Int, PlayerInstanceLocation, C>(child), PlayerInstanceLocationRepository
		where C: KeyedRepository<Int, PlayerInstanceLocation>,
			  C: MutableRepository<PlayerInstanceLocation>,
			  C: HasDirtyMarker<PlayerInstanceLocation>,
			  C: PlayerInstanceLocationRepository {
	override fun forOwner(playerInstance: PlayerInstance): PlayerInstanceLocation {
		return this.forId(playerInstance.id) ?: run {
			val result = child.forOwner(playerInstance)
			this.markStrong(result)
			result
		}
	}
}
