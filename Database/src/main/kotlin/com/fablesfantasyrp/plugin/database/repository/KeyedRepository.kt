package com.fablesfantasyrp.plugin.database.repository

interface KeyedRepository<K, T: Identifiable<K>> : Repository<T> {
	fun forId(id: K): T?
	fun forIds(ids: Sequence<K>): Collection<T> = ids.mapNotNull { forId(it) }.toList()
	fun allIds(): Collection<K>
	fun exists(id: K): Boolean = forId(id) != null
}
