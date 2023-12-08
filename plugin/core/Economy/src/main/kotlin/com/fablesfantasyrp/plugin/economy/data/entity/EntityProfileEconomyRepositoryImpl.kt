package com.fablesfantasyrp.plugin.economy.data.entity

import com.fablesfantasyrp.plugin.database.entity.MassivelyCachingEntityRepository
import com.fablesfantasyrp.plugin.database.model.HasDirtyMarker
import com.fablesfantasyrp.plugin.database.sync.repository.KeyedRepository
import com.fablesfantasyrp.plugin.database.sync.repository.MutableRepository
import com.fablesfantasyrp.plugin.profile.data.entity.Profile
import java.lang.ref.SoftReference
import kotlin.concurrent.withLock

class EntityProfileEconomyRepositoryImpl<C>(child: C)
	: MassivelyCachingEntityRepository<Int, ProfileEconomy, C>(child),
		ProfileEconomyRepository,
		EntityProfileEconomyRepository
		where C: KeyedRepository<Int, ProfileEconomy>,
			  C: MutableRepository<ProfileEconomy>,
			  C: HasDirtyMarker<ProfileEconomy>,
			  C: ProfileEconomyRepository {
	override fun forProfile(profile: Profile): ProfileEconomy {
		return this.forId(profile.id) ?: run {
			val result = child.forProfile(profile)
			lock.writeLock().withLock {
				cache[result.id] = SoftReference(result)
			}
			this.markStrong(result)
			result
		}
	}
}
