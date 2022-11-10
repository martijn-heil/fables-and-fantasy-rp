package com.fablesfantasyrp.plugin.database.entity

import com.fablesfantasyrp.plugin.database.repository.HasDirtyMarker
import com.fablesfantasyrp.plugin.database.repository.Identifiable
import com.fablesfantasyrp.plugin.database.repository.KeyedRepository
import com.fablesfantasyrp.plugin.database.repository.MutableRepository

open class MassivelyCachingEntityRepository<K, T: Identifiable<K>, C>(private val child: C) : SimpleEntityRepository<K, T, C>(child)
		where C : KeyedRepository<K, T>,
			  C : MutableRepository<T>,
			  C: HasDirtyMarker<T> {
	init {
		this.all().forEach { this.markStrong(it) }
	}

	override fun create(v: T): T {
		val result = child.create(v)
		this.markStrong(v)
		return result
	}
}
