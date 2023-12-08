package com.fablesfantasyrp.plugin.database.sync.repository

import com.fablesfantasyrp.plugin.database.model.Named

interface NamedRepository<T: Named> : Repository<T> {
	fun forName(name: String): T?
	fun allNames(): Set<String> = all().map { it.name }.toSet()
	fun nameExists(name: String): Boolean = allNames().contains(name)
}
