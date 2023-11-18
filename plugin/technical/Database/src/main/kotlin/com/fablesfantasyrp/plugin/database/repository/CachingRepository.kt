package com.fablesfantasyrp.plugin.database.repository

interface CachingRepository<T> : Repository<T>, DirtyMarker<T> {
	fun saveAllDirty()
}
