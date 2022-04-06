package com.fablesfantasyrp.plugin.database.repository

interface DirtyMarker<T> {
	fun markDirty(v: T)
}
