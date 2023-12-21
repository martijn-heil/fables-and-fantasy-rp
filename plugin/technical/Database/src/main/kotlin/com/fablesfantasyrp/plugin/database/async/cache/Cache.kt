package com.fablesfantasyrp.plugin.database.async.cache

import com.fablesfantasyrp.plugin.database.repository.DirtyMarker

interface Cache<K, T> : DirtyMarker<T> {
	suspend fun get(key: K): T?
	fun put(key: K, what: T)
	fun remove(key: K)
	fun pin(what: T)
	fun unpin(what: T)
	fun allCached(): Set<T>
	fun getDirty(): Set<T>
	fun markClean()
	fun markClean(what: T)
}
