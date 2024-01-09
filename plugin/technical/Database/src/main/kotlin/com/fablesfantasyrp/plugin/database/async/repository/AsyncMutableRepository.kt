package com.fablesfantasyrp.plugin.database.async.repository

interface AsyncMutableRepository<T> : AsyncRepository<T> {
	suspend fun destroy(v: T)
	suspend fun create(v: T): T
	suspend fun update(v: T)
	suspend fun createOrUpdate(v: T): T
}
