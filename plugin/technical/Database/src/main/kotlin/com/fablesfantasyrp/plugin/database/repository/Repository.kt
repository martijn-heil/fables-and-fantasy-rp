package com.fablesfantasyrp.plugin.database.repository

interface Repository<T> {
	fun all(): Collection<T>
}
