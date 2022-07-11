package com.fablesfantasyrp.plugin.database.entity

import com.fablesfantasyrp.plugin.database.repository.Identifiable
import com.fablesfantasyrp.plugin.database.repository.KeyedRepository
import com.fablesfantasyrp.plugin.database.repository.MutableRepository

abstract class AbstractEntityMapper<K, F, T, R>
	(private val child: R) : MutableRepository<T>, KeyedRepository<K, T>
		where F : Identifiable<K>,
			  T: F,
			  R: MutableRepository<F>,
			  R: KeyedRepository<K, F> {
	override fun destroy(v: T) = child.destroy(v)
	override fun create(v: T) = child.create(v)
	override fun update(v: T) = child.update(v)
	override fun allIds(): Collection<K> = child.allIds()
}
