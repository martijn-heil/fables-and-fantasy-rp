package com.fablesfantasyrp.plugin.database.entity

import com.fablesfantasyrp.plugin.database.CacheMarker
import com.fablesfantasyrp.plugin.database.repository.DirtyMarker
import com.fablesfantasyrp.plugin.database.repository.Identifiable
import com.fablesfantasyrp.plugin.database.repository.KeyedRepository
import com.fablesfantasyrp.plugin.database.repository.MutableRepository

interface EntityRepository<K, T: Identifiable<K>> : DirtyMarker<T>, MutableRepository<T>, KeyedRepository<K, T>, CacheMarker<T> {
	fun saveAllDirty()
}
