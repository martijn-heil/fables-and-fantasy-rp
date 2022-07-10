package com.fablesfantasyrp.plugin.database

interface CacheMarker<T> {
	fun markStrong(v: T)
	fun markWeak(v: T)
}
