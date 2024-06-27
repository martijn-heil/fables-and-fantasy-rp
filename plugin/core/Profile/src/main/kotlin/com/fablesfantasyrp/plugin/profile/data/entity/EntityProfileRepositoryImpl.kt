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
package com.fablesfantasyrp.plugin.profile.data.entity

import com.fablesfantasyrp.plugin.database.entity.MassivelyCachingEntityRepository
import com.fablesfantasyrp.plugin.database.model.HasDirtyMarker
import com.fablesfantasyrp.plugin.database.sync.repository.KeyedRepository
import com.fablesfantasyrp.plugin.database.sync.repository.MutableRepository
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
		return lock.readLock().withLock { byOwner[offlinePlayer?.uniqueId] }?.mapNotNull { this.forId(it) } ?: emptyList()
	}

	override fun create(v: Profile): Profile {
		return lock.writeLock().withLock {
			val result = super.create(v)
			byOwner.computeIfAbsent(result.owner?.uniqueId) { mutableSetOf() }.add(result.id)
			byId[result.id] = result.owner?.uniqueId
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
