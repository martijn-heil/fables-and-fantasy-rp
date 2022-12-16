package com.fablesfantasyrp.plugin.database.entity

import com.fablesfantasyrp.plugin.database.repository.HasDirtyMarker
import com.fablesfantasyrp.plugin.database.repository.Identifiable
import com.fablesfantasyrp.plugin.database.repository.KeyedRepository
import com.fablesfantasyrp.plugin.database.repository.MutableRepository
import java.lang.ref.WeakReference
import kotlin.concurrent.withLock

open class MassivelyCachingEntityRepository<K, T: Identifiable<K>, C>(child: C) : SimpleEntityRepository<K, T, C>(child)
		where C : KeyedRepository<K, T>,
			  C : MutableRepository<T>,
			  C: HasDirtyMarker<T> {

	override fun init(): SimpleEntityRepository<K, T, C> {
		super.init()
		this.all().forEach { this.markStrong(it) }
		return this
	}

	override fun create(v: T): T {
		val result = child.create(v)
		lock.writeLock().withLock {
			cache[result.id] = WeakReference(result)
		}
		this.markStrong(result)
		return result
	}
}
