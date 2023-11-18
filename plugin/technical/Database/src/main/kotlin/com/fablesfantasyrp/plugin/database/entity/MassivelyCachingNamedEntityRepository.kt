package com.fablesfantasyrp.plugin.database.entity

import com.fablesfantasyrp.plugin.database.repository.*
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
