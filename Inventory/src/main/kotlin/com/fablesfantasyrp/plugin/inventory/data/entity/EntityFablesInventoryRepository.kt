package com.fablesfantasyrp.plugin.inventory.data.entity

import com.fablesfantasyrp.plugin.database.entity.MassivelyCachingEntityRepository
import com.fablesfantasyrp.plugin.database.repository.HasDirtyMarker
import com.fablesfantasyrp.plugin.database.repository.KeyedRepository
import com.fablesfantasyrp.plugin.database.repository.MutableRepository
import com.fablesfantasyrp.plugin.playerinstance.data.entity.PlayerInstance
import java.lang.ref.SoftReference
import kotlin.concurrent.withLock

class EntityFablesInventoryRepository<C>(child: C)
	: MassivelyCachingEntityRepository<Int, PlayerInstanceInventory, C>(child), FablesInventoryRepository
		where C: KeyedRepository<Int, PlayerInstanceInventory>,
			  C: MutableRepository<PlayerInstanceInventory>,
			  C: HasDirtyMarker<PlayerInstanceInventory>,
			  C: FablesInventoryRepository {
	override fun forOwner(playerInstance: PlayerInstance): PlayerInstanceInventory {
		return this.forId(playerInstance.id) ?: run {
			val result = child.forOwner(playerInstance)
			lock.writeLock().withLock {
				cache[result.id] = SoftReference(result)
			}
			this.markStrong(result)
			result
		}
	}
}
