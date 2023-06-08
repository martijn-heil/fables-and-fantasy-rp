package com.fablesfantasyrp.plugin.database.repository

interface NamedRepository<T: Named> : Repository<T> {
	fun forName(name: String): T?
	fun allNames(): Set<String> = all().map { it.name }.toSet()
	fun nameExists(name: String): Boolean = allNames().contains(name)
}
