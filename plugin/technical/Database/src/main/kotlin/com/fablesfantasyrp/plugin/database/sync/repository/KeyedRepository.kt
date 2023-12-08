package com.fablesfantasyrp.plugin.database.sync.repository

import com.fablesfantasyrp.plugin.database.model.Identifiable

interface KeyedRepository<K, T: Identifiable<K>> : Repository<T> {
	fun forId(id: K): T?
	fun forIds(ids: Sequence<K>): Collection<T> = ids.mapNotNull { forId(it) }.toList()
	fun allIds(): Collection<K>
	fun exists(id: K): Boolean = forId(id) != null
}
