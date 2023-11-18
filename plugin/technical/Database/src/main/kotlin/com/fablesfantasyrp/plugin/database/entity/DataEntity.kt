package com.fablesfantasyrp.plugin.database.entity

import com.fablesfantasyrp.plugin.database.repository.HasDirtyMarker
import com.fablesfantasyrp.plugin.database.repository.Identifiable

interface DataEntity<K, T> : Identifiable<K>, HasDirtyMarker<T> {

}
