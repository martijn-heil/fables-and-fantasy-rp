package com.fablesfantasyrp.plugin.database.async.repository

interface AsyncRepository<T> {
	suspend fun all(): Collection<T>
	suspend fun contains(v: T): Boolean = all().contains(v)
	suspend fun containsAny(v: Collection<T>): Boolean = all().find { v.contains(it) } != null
}
