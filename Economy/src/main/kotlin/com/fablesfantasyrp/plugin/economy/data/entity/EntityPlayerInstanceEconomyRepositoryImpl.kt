package com.fablesfantasyrp.plugin.economy.data.entity

import com.fablesfantasyrp.plugin.database.entity.MassivelyCachingEntityRepository
import com.fablesfantasyrp.plugin.database.repository.HasDirtyMarker
import com.fablesfantasyrp.plugin.database.repository.KeyedRepository
import com.fablesfantasyrp.plugin.database.repository.MutableRepository
import com.fablesfantasyrp.plugin.playerinstance.data.entity.PlayerInstance
import java.lang.ref.WeakReference
import kotlin.concurrent.withLock

class EntityPlayerInstanceEconomyRepositoryImpl<C>(child: C)
	: MassivelyCachingEntityRepository<Int, PlayerInstanceEconomy, C>(child),
		PlayerInstanceEconomyRepository,
		EntityPlayerInstanceEconomyRepository
		where C: KeyedRepository<Int, PlayerInstanceEconomy>,
			  C: MutableRepository<PlayerInstanceEconomy>,
			  C: HasDirtyMarker<PlayerInstanceEconomy>,
			  C: PlayerInstanceEconomyRepository {
	override fun forPlayerInstance(playerInstance: PlayerInstance): PlayerInstanceEconomy {
		return this.forId(playerInstance.id) ?: run {
			val result = child.forPlayerInstance(playerInstance)
			lock.writeLock().withLock {
				cache[result.id] = WeakReference(result)
			}
			this.markStrong(result)
			result
		}
	}
}
