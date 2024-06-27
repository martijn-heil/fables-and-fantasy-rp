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

import com.fablesfantasyrp.plugin.database.sync.repository.MutableRepository
import java.util.concurrent.locks.ReadWriteLock
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.withLock

open class SimpleSetRepository<T> : MutableRepository<T> {
	protected val contents = HashSet<T>()
	protected val lock: ReadWriteLock = ReentrantReadWriteLock()
	override fun createOrUpdate(v: T): T = lock.readLock().withLock { contents.add(v); return v }
	override fun all(): Collection<T> = lock.readLock().withLock { contents }
	override fun destroy(v: T) { lock.writeLock().withLock { contents.remove(v) } }
	override fun create(v: T): T { lock.writeLock().withLock { contents.add(v); return v } }
	override fun update(v: T) { lock.writeLock().withLock { contents.add(v) } }
}
