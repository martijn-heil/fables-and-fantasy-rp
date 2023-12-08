package com.fablesfantasyrp.plugin.database.entity

import com.fablesfantasyrp.plugin.database.model.HasDirtyMarker
import com.fablesfantasyrp.plugin.database.model.Identifiable

interface DataEntity<K, T> : Identifiable<K>, HasDirtyMarker<T> {

}
