package com.fablesfantasyrp.plugin.database.model

import com.fablesfantasyrp.plugin.database.repository.DirtyMarker

interface HasDirtyMarker<T> {
	var dirtyMarker: DirtyMarker<T>?
}
