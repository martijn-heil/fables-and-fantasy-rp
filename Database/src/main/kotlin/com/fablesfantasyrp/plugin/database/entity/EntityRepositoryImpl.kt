package com.fablesfantasyrp.plugin.database.entity

import com.fablesfantasyrp.plugin.database.repository.Identifiable
import com.fablesfantasyrp.plugin.database.repository.KeyedRepository
import com.fablesfantasyrp.plugin.database.repository.MutableRepository
import java.lang.ref.WeakReference

class EntityRepositoryImpl<K, T: Identifiable<K>, C>(private var child: C) : EntityRepository<K, T>
		where C : KeyedRepository<K, T>, C : MutableRepository<T> {
	private val cache = HashMap<K, WeakReference<T>>()
	private val strongCache = HashSet<T>()
	private val dirty = LinkedHashSet<T>()

	fun markStrong(v: T) {
		strongCache.add(v)
	}

	fun markWeak(v: T) {
		strongCache.remove(v)
	}

	override fun saveAllDirty() {
		synchronized(this) {
			dirty.forEach { child.update(it) }
			dirty.clear()
		}
	}

	override fun create(v: T) {
		child.create(v)
		cache[v.id] = WeakReference(v)
	}

	private fun saveNWeakDirty(n: Int) {
		synchronized(this) {
			val entries = dirty.asSequence().filter { !strongCache.contains(it) }.take(n).toList()
			entries.forEach { this.update(it) }
		}
	}

	override fun markDirty(v: T) {
		synchronized(this) {
			dirty.add(v)
		}
	}

	override fun update(v: T) {
		synchronized(this) {
			child.update(v)
			dirty.remove(v)
		}
	}

	override fun destroy(v: T) {
		synchronized(this) {
			cache.remove(v.id)
			strongCache.remove(v)
			child.destroy(v)
		}
	}

	override fun allIds(): Collection<K> = child.allIds()
	override fun all(): Collection<T> = child.allIds().map { this.forId(it)!! }
	override fun forId(id: K): T? = fromCache(id) ?: child.forId(id)

	private fun fromCache(id: K): T? {
		synchronized(this) {
			val maybe = cache[id]?.get()
			if (maybe == null) cache.remove(id)
			return maybe
		}
	}
}
