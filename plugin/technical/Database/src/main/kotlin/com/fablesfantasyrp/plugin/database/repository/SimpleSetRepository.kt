package com.fablesfantasyrp.plugin.database.repository

import com.fablesfantasyrp.plugin.database.sync.repository.MutableRepository
import java.util.concurrent.locks.ReadWriteLock
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.withLock

open class SimpleSetRepository<T> : MutableRepository<T> {
	protected val contents = HashSet<T>()
	protected val lock: ReadWriteLock = ReentrantReadWriteLock()

	override fun all(): Collection<T> = lock.readLock().withLock { contents }
	override fun destroy(v: T) { lock.writeLock().withLock { contents.remove(v) } }
	override fun create(v: T): T { lock.writeLock().withLock { contents.add(v); return v } }
	override fun update(v: T) { lock.writeLock().withLock { contents.add(v) } }
}
