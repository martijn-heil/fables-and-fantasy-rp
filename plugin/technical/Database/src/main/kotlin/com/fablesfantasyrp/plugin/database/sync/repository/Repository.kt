package com.fablesfantasyrp.plugin.database.sync.repository

interface Repository<T> {
	fun all(): Collection<T>
}
