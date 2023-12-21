package com.fablesfantasyrp.plugin.database.async.cache

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.lang.ref.SoftReference

class CacheImpl<K, T>(private val load: (K) -> T?) : Cache<K, T> {
	private val map = HashMap<K, SoftReference<T>>()
	private val pinned = HashSet<T>()
	private val dirty = LinkedHashSet<T>()

	override suspend fun get(key: K): T? {
		val value = map[key]?.get()

		return if (value == null) {
			val newValue = withContext(Dispatchers.IO) { load(key) }

			val secondTry = map[key]?.get()
			if (secondTry != null) return secondTry

			if (newValue != null) {
				map[key] = SoftReference(newValue)
				newValue
			} else {
				map.remove(key)
				null
			}
		} else {
			value
		}
	}

	override fun pin(what: T) { pinned.add(what) }
	override fun unpin(what: T) { pinned.remove(what) }
	override fun markDirty(v: T) { dirty.add(v) }
	override fun put(key: K, what: T) { map[key] = SoftReference(what) }
	override fun allCached(): Set<T> = map.values.mapNotNull { it.get() }.toSet()
	override fun getDirty(): Set<T> = dirty
	override fun markClean() = dirty.clear()
	override fun markClean(what: T) { dirty.remove(what) }

	override fun remove(key: K) { map.remove(key) }
}
