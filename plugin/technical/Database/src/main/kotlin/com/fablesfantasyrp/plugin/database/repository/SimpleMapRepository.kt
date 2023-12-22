package com.fablesfantasyrp.plugin.database.repository

import com.fablesfantasyrp.plugin.database.model.Identifiable
import com.fablesfantasyrp.plugin.database.sync.repository.KeyedRepository
import com.fablesfantasyrp.plugin.database.sync.repository.MutableRepository
import java.util.concurrent.locks.ReadWriteLock
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.withLock

open class SimpleMapRepository<K, T: Identifiable<K>> : MutableRepository<T>, KeyedRepository<K, T> {
	protected val contents = HashMap<K, T>()
	protected val lock: ReadWriteLock = ReentrantReadWriteLock()

	override fun forId(id: K): T? = lock.readLock().withLock { contents[id] }
	override fun allIds(): Collection<K> = lock.readLock().withLock { contents.keys }
	override fun createOrUpdate(v: T): T = lock.writeLock().withLock { contents[v.id] = v; return v }
	override fun all(): Collection<T> = lock.readLock().withLock { contents.values }
	override fun destroy(v: T) { lock.writeLock().withLock { contents.remove(v.id) } }
	override fun create(v: T): T { lock.writeLock().withLock { contents[v.id] = v }; return v }
	override fun update(v: T) { lock.writeLock().withLock { contents[v.id] = v } }
}
