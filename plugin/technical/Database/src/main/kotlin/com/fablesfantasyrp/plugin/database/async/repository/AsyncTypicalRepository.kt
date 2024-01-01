package com.fablesfantasyrp.plugin.database.async.repository

import com.fablesfantasyrp.plugin.database.CacheMarker
import com.fablesfantasyrp.plugin.database.repository.DirtyMarker
import com.fablesfantasyrp.plugin.database.model.Identifiable
import com.fablesfantasyrp.plugin.database.sync.repository.KeyedRepository
import com.fablesfantasyrp.plugin.database.sync.repository.MutableRepository

interface AsyncTypicalRepository<K, T: Identifiable<K>> : DirtyMarker<T>,
	AsyncMutableRepository<T>, AsyncKeyedRepository<K, T>, CacheMarker<T> {
	fun saveAllDirty()
}