package com.fablesfantasyrp.plugin.inventory.data.entity

import com.fablesfantasyrp.plugin.database.entity.MassivelyCachingEntityRepository
import com.fablesfantasyrp.plugin.database.repository.HasDirtyMarker
import com.fablesfantasyrp.plugin.database.repository.KeyedRepository
import com.fablesfantasyrp.plugin.database.repository.MutableRepository
import com.fablesfantasyrp.plugin.profile.data.entity.Profile
import java.lang.ref.SoftReference
import kotlin.concurrent.withLock

class EntityFablesInventoryRepository<C>(child: C)
	: MassivelyCachingEntityRepository<Int, ProfileInventory, C>(child), FablesInventoryRepository
		where C: KeyedRepository<Int, ProfileInventory>,
			  C: MutableRepository<ProfileInventory>,
			  C: HasDirtyMarker<ProfileInventory>,
			  C: FablesInventoryRepository {
	override fun forOwner(profile: Profile): ProfileInventory {
		return this.forId(profile.id) ?: run {
			val result = child.forOwner(profile)
			lock.writeLock().withLock {
				cache[result.id] = SoftReference(result)
			}
			this.markStrong(result)
			result
		}
	}
}
