package com.fablesfantasyrp.plugin.database.async.repository.base

import com.fablesfantasyrp.plugin.database.async.repository.AsyncKeyedRepository
import com.fablesfantasyrp.plugin.database.async.repository.AsyncMutableRepository
import com.fablesfantasyrp.plugin.database.entity.HasDestroyHandler
import com.fablesfantasyrp.plugin.database.model.HasDirtyMarker
import com.fablesfantasyrp.plugin.database.model.Identifiable
import com.fablesfantasyrp.plugin.database.repository.DirtyMarker
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.bukkit.Bukkit
import java.lang.ref.SoftReference

open class AsyncTypicalRepository<K, T: Identifiable<K>, C>(protected var child: C)
	: AsyncMutableRepository<T>, AsyncKeyedRepository<K, T>, HasDestroyHandler<T>, DirtyMarker<T>
		where C : AsyncKeyedRepository<K, T>,
			  C : AsyncMutableRepository<T>,
			  C : HasDirtyMarker<T> {
	protected val lock: Mutex = Mutex()

	protected val cache = HashMap<K, SoftReference<T>>()

	protected val strongCache = HashSet<T>()
	protected val strongCacheLock: Mutex = Mutex()

	protected val dirty = LinkedHashSet<T>()
	protected val dirtyLock: Mutex = Mutex()

	protected val destroyHandlers: MutableCollection<(T) -> Unit> = ArrayList()

	open fun init() {
		child.dirtyMarker = this
	}

	suspend fun markStrong(v: T) { strongCacheLock.withLock { strongCache.add(v) } }
	suspend fun markWeak(v: T) { strongCacheLock.withLock { strongCache.remove(v) } }

	override fun markDirty(v: T) {
		runBlocking { dirtyLock.withLock { dirty.add(v) } }
	}

	suspend fun saveAllDirty() {
		val dirtyCopy = dirtyLock.withLock { dirty.toSet() }
		dirtyCopy.asFlow().onEach { child.update(it) }
	}

	suspend fun saveAll() {
		val all = lock.withLock { cache.mapNotNull { it.value.get() } }
		all.asFlow().onEach { update(it) }
	}

	override suspend fun create(v: T): T {
		val result = child.create(v)
		lock.withLock {
			cache[result.id] = SoftReference(result)
		}
		return result
	}

	private suspend fun saveNWeakDirty(n: Int) {
		val entries = lock.withLock { dirty.asSequence().filter { !strongCache.contains(it) }.take(n).toList() }
		entries.asFlow().onEach { update(it) }
	}

	override suspend fun update(v: T) {
		child.update(v)
		dirtyLock.withLock { dirty.remove(v) }
	}

	override suspend fun destroy(v: T) {
		child.destroy(v)
		lock.withLock {
			destroyHandlers.forEach { it(v) }
			cache.remove(v.id)
			strongCache.remove(v)
		}
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
		Bukkit.getLogger().info("trying to lock, isLocked = ${lock.isLocked}")
		return lock.withLock {
			Bukkit.getLogger().info("locked")
			val result = cache[id]?.get() ?: run {
				Bukkit.getLogger().info("didnt get from cache, retrieving from child")
				delay(1000)
				val obj = null //child.forId(id) ?: return@run null
				Bukkit.getLogger().info("got $obj from child")
				cache[id] = SoftReference(obj)
				obj
			}
			Bukkit.getLogger().info("result: $result")
			result
		}
	}

	protected fun deduplicate(entities: Collection<T>): Collection<T> {
		return entities.map { deduplicate(it) }
	}

	protected fun deduplicate(v: T)
		= cache.merge(v.id, SoftReference(v)) { a, b -> if (a.get() != null) a else b }!!.get()!!

	override fun onDestroy(callback: (T) -> Unit) {
		destroyHandlers.add(callback)
	}
}
