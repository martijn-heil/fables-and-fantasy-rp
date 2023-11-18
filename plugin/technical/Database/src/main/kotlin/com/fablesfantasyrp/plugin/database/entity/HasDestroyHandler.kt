package com.fablesfantasyrp.plugin.database.entity

interface HasDestroyHandler<T> {
	fun onDestroy(callback: (T) -> Unit)
}
