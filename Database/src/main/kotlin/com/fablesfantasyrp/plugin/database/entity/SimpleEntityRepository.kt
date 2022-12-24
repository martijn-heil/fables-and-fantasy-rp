package com.fablesfantasyrp.plugin.database.entity

import com.fablesfantasyrp.plugin.database.repository.HasDirtyMarker
import com.fablesfantasyrp.plugin.database.repository.Identifiable
import com.fablesfantasyrp.plugin.database.repository.KeyedRepository
import com.fablesfantasyrp.plugin.database.repository.MutableRepository
import java.lang.ref.SoftReference
import java.util.concurrent.locks.ReadWriteLock
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.withLock

open class SimpleEntityRepository<K, T: Identifiable<K>, C>(protected var child: C) : EntityRepository<K, T>
		where C : KeyedRepository<K, T>,
			  C : MutableRepository<T>,
			  C: HasDirtyMarker<T> {
	protected val cache = HashMap<K, SoftReference<T>>()
	protected val strongCache = HashSet<T>()
	protected val dirty = LinkedHashSet<T>()
	protected val lock: ReadWriteLock = ReentrantReadWriteLock()

	open fun init() {
		child.dirtyMarker = this
	}

	override fun markStrong(v: T) { lock.writeLock().withLock { strongCache.add(v) } }
	override fun markWeak(v: T) { lock.writeLock().withLock { strongCache.remove(v) } }

	override fun saveAllDirty() {
		lock.writeLock().withLock {
			dirty.forEach { child.update(it) }
			dirty.clear()
		}
	}

	fun saveAll() {
		lock.writeLock().withLock {
			cache.mapNotNull { it.value.get() }.forEach { this.update(it) }
			dirty.clear()
		}
	}

	override fun create(v: T): T {
		val result = child.create(v)
		lock.writeLock().withLock {
			cache[result.id] = SoftReference(result)
		}
		return result
	}

	private fun saveNWeakDirty(n: Int) {
		lock.writeLock().withLock {
			val entries = dirty.asSequence().filter { !strongCache.contains(it) }.take(n).toList()
			entries.forEach { this.update(it) }
		}
	}

	override fun markDirty(v: T) {
		lock.writeLock().withLock {
			dirty.add(v)
		}
	}

	override fun update(v: T) {
		lock.writeLock().withLock {
			child.update(v)
			dirty.remove(v)
		}
	}

	override fun destroy(v: T) {
		lock.writeLock().withLock {
			cache.remove(v.id)
			strongCache.remove(v)
			child.destroy(v)
		}
	}

	override fun allIds(): Collection<K> = child.allIds()
	override fun all(): Collection<T> = child.allIds().mapNotNull { this.forId(it) }
	override fun forId(id: K): T? = fromCache(id) ?: run {
		lock.writeLock().withLock {
			fromCache(id) ?: run {
				val obj = child.forId(id)
				cache[id] = SoftReference(obj)
				obj
			}
		}
	}

	protected fun fromCache(id: K): T? {
		lock.readLock().withLock {
			return cache[id]?.get()
		}
	}
}
