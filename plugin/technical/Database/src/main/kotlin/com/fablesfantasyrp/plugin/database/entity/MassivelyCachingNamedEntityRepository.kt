package com.fablesfantasyrp.plugin.database.entity

import com.fablesfantasyrp.plugin.database.model.HasDirtyMarker
import com.fablesfantasyrp.plugin.database.model.Identifiable
import com.fablesfantasyrp.plugin.database.model.Named
import com.fablesfantasyrp.plugin.database.sync.repository.KeyedRepository
import com.fablesfantasyrp.plugin.database.sync.repository.MutableRepository
import com.fablesfantasyrp.plugin.database.sync.repository.NamedRepository
import java.lang.ref.SoftReference
import kotlin.concurrent.withLock

open class MassivelyCachingNamedEntityRepository<K, T, C>(child: C) : SimpleNamedEntityRepository<K, T, C>(child)
		where T : Identifiable<K>,
			  T : Named,
			  C : KeyedRepository<K, T>,
			  C : MutableRepository<T>,
			  C : NamedRepository<T>,
			  C : HasDirtyMarker<T> {

	override fun init() {
		super.init()
		this.all().forEach { this.markStrong(it) }
	}

	override fun create(v: T): T {
		val result = super.create(v)
		lock.writeLock().withLock {
			cache[result.id] = SoftReference(result)
		}
		this.markStrong(result)
		return result
	}
}
