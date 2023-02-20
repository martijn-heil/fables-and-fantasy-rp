package com.fablesfantasyrp.plugin.database.repository

interface KeyedRepository<K, T: Identifiable<K>> : Repository<T> {
	fun forId(id: K): T?
	fun allIds(): Collection<K>
	fun exists(id: K): Boolean = forId(id) != null
}
