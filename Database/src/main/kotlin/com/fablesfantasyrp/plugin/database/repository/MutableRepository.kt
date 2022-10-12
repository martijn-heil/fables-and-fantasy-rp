package com.fablesfantasyrp.plugin.database.repository

interface MutableRepository<T> : Repository<T> {
	fun destroy(v: T)
	fun create(v: T): T
	fun update(v: T)
}
