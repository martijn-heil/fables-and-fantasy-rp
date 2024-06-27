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
package com.fablesfantasyrp.plugin.database.async.repository.base

import com.fablesfantasyrp.plugin.database.CacheMarker
import com.fablesfantasyrp.plugin.database.async.repository.AsyncKeyedRepository
import com.fablesfantasyrp.plugin.database.model.HasCacheMarker
import com.fablesfantasyrp.plugin.database.model.Identifiable
import java.lang.ref.SoftReference

open class AsyncTypicalImmutableRepository<K, T: Identifiable<K>, C>(protected var child: C)
	: AsyncKeyedRepository<K, T>, CacheMarker<T>
		where C : AsyncKeyedRepository<K, T>,
			  C : HasCacheMarker<T> {
	protected data class LookupResult<T>(val reference: SoftReference<T>?)

	private val EMPTY_LOOKUP_RESULT = LookupResult<T>(null)

	protected val cache = HashMap<K, LookupResult<T>>()
	protected val strongCache = HashSet<T>()

	open fun init() {
		child.cacheMarker = this
	}

	override fun markStrong(v: T) { strongCache.add(v) }
	override fun markWeak(v: T) { strongCache.remove(v) }

	override suspend fun allIds(): Collection<K> = child.allIds()

	override suspend fun all(): Collection<T> = child.allIds().mapNotNull { this.forId(it) }
	override suspend fun forId(id: K): T? {
		val lookupResult = cache[id]
		val entity = lookupResult?.reference?.get()

		if (entity != null) {
			return entity
		} else if (lookupResult == EMPTY_LOOKUP_RESULT) { // Entity was already looked up earlier but doesn't exist
			return null
		} else {
			val obj = child.forId(id) ?: run {
				cache[id] = EMPTY_LOOKUP_RESULT
				return null
			}
			return deduplicate(obj)
		}
	}

	protected fun deduplicate(entities: Collection<T>): Collection<T>
		= entities.map { deduplicate(it) }

	protected fun deduplicate(v: T)
		= cache.merge(v.id, LookupResult(SoftReference(v))) { a, b -> if (a.reference?.get() != null) a else b }!!.reference!!.get()!!
}
