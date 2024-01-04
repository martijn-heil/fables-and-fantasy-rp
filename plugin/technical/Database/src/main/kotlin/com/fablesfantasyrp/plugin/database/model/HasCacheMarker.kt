package com.fablesfantasyrp.plugin.database.model

import com.fablesfantasyrp.plugin.database.CacheMarker

interface HasCacheMarker<T> {
	var cacheMarker: CacheMarker<T>?
}
