package com.fablesfantasyrp.plugin.location.data.entity

import com.fablesfantasyrp.plugin.database.entity.MassivelyCachingEntityRepository
import com.fablesfantasyrp.plugin.database.model.HasDirtyMarker
import com.fablesfantasyrp.plugin.database.sync.repository.KeyedRepository
import com.fablesfantasyrp.plugin.database.sync.repository.MutableRepository
import com.fablesfantasyrp.plugin.profile.data.entity.Profile
import java.lang.ref.SoftReference
import kotlin.concurrent.withLock

class EntityProfileLocationRepository<C>(child: C)
	: MassivelyCachingEntityRepository<Int, ProfileLocation, C>(child), ProfileLocationRepository
		where C: KeyedRepository<Int, ProfileLocation>,
			  C: MutableRepository<ProfileLocation>,
			  C: HasDirtyMarker<ProfileLocation>,
			  C: ProfileLocationRepository {
	override fun forOwner(profile: Profile): ProfileLocation {
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
