package com.fablesfantasyrp.plugin.database.repository

interface Repository<T> {
	fun save(v: T)
	fun destroy(v: T)
}
