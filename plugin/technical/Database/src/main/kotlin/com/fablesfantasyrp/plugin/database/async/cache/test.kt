package com.fablesfantasyrp.plugin.database.async.cache

import com.github.benmanes.caffeine.cache.Cache
import com.github.benmanes.caffeine.cache.Caffeine
import com.google.common.cache.RemovalCause
import java.util.concurrent.ConcurrentHashMap

class IndexedCache<K, V> private constructor(builder: Builder<K, V>) : Cache<K, V> by builder.caf!!.build() {
	private val indexes: Map<Class<*>, Map<Any, Set<K>>>

	init {
		indexes = builder.indexes

		Caffeine.newBuilder()
			.weakValues()
			.evictionListener { key, value, cause ->  }
	}

	fun <R> invalidateAllWithIndex(clazz: Class<R>, value: R) {
		this.invalidateAll(indexes[clazz]!!.getOrDefault(value, HashSet()))
	}

	class Builder<K, V> {

	}
}
