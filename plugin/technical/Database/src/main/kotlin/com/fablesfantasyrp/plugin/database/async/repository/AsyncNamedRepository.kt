package com.fablesfantasyrp.plugin.database.async.repository

import com.fablesfantasyrp.plugin.database.model.Named

interface AsyncNamedRepository<T: Named> : AsyncRepository<T> {
	suspend fun forName(name: String): T?
	suspend fun allNames(): Set<String> = all().map { it.name }.toSet()
	suspend fun nameExists(name: String): Boolean = allNames().contains(name)
}
