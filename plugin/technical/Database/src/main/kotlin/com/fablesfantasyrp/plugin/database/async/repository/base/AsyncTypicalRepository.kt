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
import com.fablesfantasyrp.plugin.database.async.repository.AsyncMutableRepository
import com.fablesfantasyrp.plugin.database.entity.HasDestroyHandler
import com.fablesfantasyrp.plugin.database.model.HasCacheMarker
import com.fablesfantasyrp.plugin.database.model.HasDirtyMarker
import com.fablesfantasyrp.plugin.database.model.Identifiable
import com.fablesfantasyrp.plugin.database.repository.DirtyMarker
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import java.lang.ref.SoftReference

open class AsyncTypicalRepository<K, T: Identifiable<K>, C>(child: C)
	: AsyncTypicalImmutableRepository<K, T, C>(child),
	AsyncMutableRepository<T>, AsyncKeyedRepository<K, T>, HasDestroyHandler<T>, DirtyMarker<T>, CacheMarker<T>
		where C : AsyncKeyedRepository<K, T>,
			  C : AsyncMutableRepository<T>,
			  C : HasDirtyMarker<T>, C: HasCacheMarker<T> {
	protected val dirty = LinkedHashSet<T>()

	protected val destroyHandlers: MutableCollection<(T) -> Unit> = ArrayList()

	override fun init() {
		super.init()
		child.dirtyMarker = this
	}

	override fun markStrong(v: T) { strongCache.add(v) }
	override fun markWeak(v: T) { strongCache.remove(v) }

	override fun markDirty(v: T) { dirty.add(v) }

	suspend fun saveAllDirty() {
		val dirtyCopy = dirty.toSet()
		dirtyCopy.asFlow().onEach { child.update(it) }.collect()
	}

	suspend fun saveAll() {
		val all = cache.mapNotNull { it.value.reference?.get() }
		all.asFlow().onEach { update(it) }.collect()
	}

	override suspend fun create(v: T): T {
		val result = child.create(v)
		cache[result.id] = LookupResult(SoftReference(result))
		return result
	}

	private suspend fun saveNWeakDirty(n: Int) {
		val entries = dirty.asSequence().filter { !strongCache.contains(it) }.take(n).toList()
		entries.asFlow().onEach { update(it) }.collect()
	}

	override suspend fun update(v: T) {
		child.update(v)
		dirty.remove(v)
	}

	override suspend fun destroy(v: T) {
		child.destroy(v)
		destroyHandlers.forEach { it(v) }
		cache.remove(v.id)
		strongCache.remove(v)
	}

	override suspend fun createOrUpdate(v: T): T {
		return if (v.id == 0 || !this.exists(v.id)) {
			this.create(v)
		} else {
			this.update(v)
			v
		}
	}

	override fun onDestroy(callback: (T) -> Unit) {
		destroyHandlers.add(callback)
	}
}
