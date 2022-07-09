package com.fablesfantasyrp.plugin.database.entity

import com.fablesfantasyrp.plugin.database.repository.DirtyMarker
import com.fablesfantasyrp.plugin.database.repository.Identifiable
import com.fablesfantasyrp.plugin.database.repository.MutableRepository

interface EntityRepository<K, T: Identifiable<K>> : DirtyMarker<T>, MutableRepository<T> {
	fun forId(id: K): T?
	fun allIds(): Collection<K>
	fun saveAllDirty()
}
