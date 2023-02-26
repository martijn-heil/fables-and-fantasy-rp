package com.fablesfantasyrp.plugin.profile.data.entity

import com.fablesfantasyrp.plugin.database.entity.MassivelyCachingEntityRepository
import com.fablesfantasyrp.plugin.database.repository.HasDirtyMarker
import com.fablesfantasyrp.plugin.database.repository.KeyedRepository
import com.fablesfantasyrp.plugin.database.repository.MutableRepository
import com.fablesfantasyrp.plugin.utils.withLock
import org.bukkit.OfflinePlayer
import java.util.*

class EntityProfileRepositoryImpl<C>(child: C) :
		MassivelyCachingEntityRepository<Int, Profile, C>(child), EntityProfileRepository
		where C: KeyedRepository<Int, Profile>,
			  C: MutableRepository<Profile>,
			  C: HasDirtyMarker<Profile>,
			  C: ProfileRepository {
	private val byOwner = HashMap<UUID?, MutableSet<Int>>()
	private val byId = HashMap<Int, UUID?>()

	override fun init() {
		super.init()
		this.all().forEach {
			byOwner.computeIfAbsent(it.owner?.uniqueId) { mutableSetOf() }.add(it.id)
			byId[it.id] = it.owner?.uniqueId
		}
	}

	override fun allForOwner(offlinePlayer: OfflinePlayer?): Collection<Profile> {
		return lock.readLock().withLock {
			byOwner[offlinePlayer?.uniqueId]?.mapNotNull { this.forId(it) } ?: emptyList()
		}
	}

	override fun create(v: Profile): Profile {
		return lock.writeLock().withLock {
			val result = super.create(v)
			byOwner.computeIfAbsent(v.owner?.uniqueId) { mutableSetOf() }.add(v.id)
			byId[v.id] = v.owner?.uniqueId
			result
		}
	}

	override fun destroy(v: Profile) {
		lock.writeLock().withLock {
			super.destroy(v)
			byOwner[v.owner?.uniqueId]?.remove(v.id)
			byId.remove(v.id)
		}
	}

	override fun markDirty(v: Profile, what: String) {
		lock.writeLock().withLock {
			if (what == "owner" && byId.containsKey(v.id)) {
				byOwner[byId[v.id]]?.remove(v.id)
				byOwner.computeIfAbsent(v.owner?.uniqueId) { mutableSetOf() }.add(v.id)
				byId[v.id] = v.owner?.uniqueId
			}
			super<MassivelyCachingEntityRepository>.markDirty(v)
		}
	}
}
