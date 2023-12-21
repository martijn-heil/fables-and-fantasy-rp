package com.fablesfantasyrp.plugin.database.sync.repository

interface MutableRepository<T> : Repository<T> {
	fun destroy(v: T)
	fun create(v: T): T
	fun update(v: T)
	fun createOrUpdate(v: T): T
	fun contains(v: T): Boolean = all().contains(v)
	fun containsAny(v: Collection<T>): Boolean = all().find { v.contains(it) } != null
}
