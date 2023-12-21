package com.fablesfantasyrp.plugin.database.async.repository

interface AsyncRepository<T> {
	suspend fun all(): Collection<T>
}
