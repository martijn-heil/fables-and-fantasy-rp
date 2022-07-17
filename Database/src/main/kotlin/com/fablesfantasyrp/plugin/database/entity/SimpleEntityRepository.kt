package com.fablesfantasyrp.plugin.database.entity

import com.fablesfantasyrp.plugin.database.repository.HasDirtyMarker
import com.fablesfantasyrp.plugin.database.repository.Identifiable
import com.fablesfantasyrp.plugin.database.repository.KeyedRepository
import com.fablesfantasyrp.plugin.database.repository.MutableRepository
import java.lang.ref.WeakReference
import java.util.concurrent.locks.ReadWriteLock
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.withLock

open class SimpleEntityRepository<K, T: Identifiable<K>, C>(private var child: C) : EntityRepository<K, T>
		where C : KeyedRepository<K, T>,
			  C : MutableRepository<T>,
			  C: HasDirtyMarker<T> {
	private val cache = HashMap<K, WeakReference<T>>()
	private val strongCache = HashSet<T>()
	private val dirty = LinkedHashSet<T>()
	private val lock: ReadWriteLock = ReentrantReadWriteLock()

	fun init(): SimpleEntityRepository<K, T, C> {
		child.dirtyMarker = this
		return this
	}

	override fun markStrong(v: T) { lock.writeLock().withLock { strongCache.add(v) } }
	override fun markWeak(v: T) { lock.writeLock().withLock { strongCache.remove(v) } }

	override fun saveAllDirty() {
		lock.writeLock().withLock {
			dirty.forEach { child.update(it) }
			dirty.clear()
		}
	}

	override fun create(v: T) {
		child.create(v)
		lock.writeLock().withLock {
			cache[v.id] = WeakReference(v)
		}
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
	override fun all(): Collection<T> = child.allIds().map { this.forId(it)!! }
	override fun forId(id: K): T? = fromCache(id) ?: run {
		lock.writeLock().withLock {
			fromCache(id) ?: run {
				val obj = child.forId(id)
				cache[id] = WeakReference(obj)
				obj
			}
		}
	}

	private fun fromCache(id: K): T? {
		lock.readLock().withLock {
			return cache[id]?.get()
		}
	}
}