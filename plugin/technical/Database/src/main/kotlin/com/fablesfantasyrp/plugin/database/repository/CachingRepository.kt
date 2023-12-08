package com.fablesfantasyrp.plugin.database.repository

import com.fablesfantasyrp.plugin.database.sync.repository.Repository

interface CachingRepository<T> : Repository<T>, DirtyMarker<T> {
	fun saveAllDirty()
}
