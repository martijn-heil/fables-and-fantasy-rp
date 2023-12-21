package com.fablesfantasyrp.plugin.database.async.repository

import com.fablesfantasyrp.plugin.database.model.Identifiable
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.toList

interface AsyncKeyedRepository<K, T: Identifiable<K>> : AsyncRepository<T> {
	suspend fun forId(id: K): T?
	suspend fun forIds(ids: Sequence<K>): Collection<T> = ids.asFlow().mapNotNull { forId(it) }.toList()
	suspend fun allIds(): Collection<K>
	suspend fun exists(id: K): Boolean = forId(id) != null
}
