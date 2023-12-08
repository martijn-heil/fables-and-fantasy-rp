package com.fablesfantasyrp.plugin.database.entity

import com.fablesfantasyrp.plugin.database.model.Identifiable
import com.fablesfantasyrp.plugin.database.repository.SimpleMapRepository

open class SimpleMapEntityRepository<K, T: Identifiable<K>> : SimpleMapRepository<K, T>(), EntityRepository<K, T> {
	override fun saveAllDirty() {}
	override fun markDirty(v: T) {}
	override fun markStrong(v: T) {}
	override fun markWeak(v: T) {}
}
