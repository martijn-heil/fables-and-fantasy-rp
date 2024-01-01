package com.fablesfantasyrp.plugin.database.async.repository.base

import com.fablesfantasyrp.plugin.database.async.repository.AsyncKeyedRepository
import com.fablesfantasyrp.plugin.database.async.repository.AsyncMutableRepository
import com.fablesfantasyrp.plugin.database.entity.HasDestroyHandler
import com.fablesfantasyrp.plugin.database.model.HasDirtyMarker
import com.fablesfantasyrp.plugin.database.model.Identifiable
import com.fablesfantasyrp.plugin.database.repository.DirtyMarker
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import java.lang.ref.SoftReference

open class AsyncTypicalRepository<K, T: Identifiable<K>, C>(protected var child: C)
	: AsyncMutableRepository<T>, AsyncKeyedRepository<K, T>, HasDestroyHandler<T>, DirtyMarker<T>
		where C : AsyncKeyedRepository<K, T>,
			  C : AsyncMutableRepository<T>,
			  C : HasDirtyMarker<T> {
	protected data class LookupResult<T>(val reference: SoftReference<T>?)

	private val EMPTY_LOOKUP_RESULT = LookupResult<T>(null)

	protected val cache = HashMap<K, LookupResult<T>>()
	protected val strongCache = HashSet<T>()
	protected val dirty = LinkedHashSet<T>()

	protected val destroyHandlers: MutableCollection<(T) -> Unit> = ArrayList()

	open fun init() {
		child.dirtyMarker = this
	}

	suspend fun markStrong(v: T) { strongCache.add(v) }
	suspend fun markWeak(v: T) { strongCache.remove(v) }

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

	override suspend fun allIds(): Collection<K> = child.allIds()

	override suspend fun createOrUpdate(v: T): T {
		return if (v.id == 0 || !this.exists(v.id)) {
			this.create(v)
		} else {
			this.update(v)
			v
		}
	}

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

	override fun onDestroy(callback: (T) -> Unit) {
		destroyHandlers.add(callback)
	}
}
