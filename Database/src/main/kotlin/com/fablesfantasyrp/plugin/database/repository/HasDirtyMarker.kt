package com.fablesfantasyrp.plugin.database.repository

interface HasDirtyMarker<T> {
	var dirtyMarker: DirtyMarker<T>?
}
