/*
 * Fables and Fantasy RP kotlin plugins.
 * Copyright (C) 2024  Martijn Heil
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
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
