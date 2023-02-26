package com.fablesfantasyrp.plugin.database.repository

interface DirtyMarker<T> {
	fun markDirty(v: T)
	fun markDirty(v: T, what: String) = markDirty(v)
}
