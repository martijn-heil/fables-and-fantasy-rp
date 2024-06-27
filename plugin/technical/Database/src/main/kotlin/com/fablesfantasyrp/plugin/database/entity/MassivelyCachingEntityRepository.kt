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
package com.fablesfantasyrp.plugin.database.entity

import com.fablesfantasyrp.plugin.database.model.HasDirtyMarker
import com.fablesfantasyrp.plugin.database.model.Identifiable
import com.fablesfantasyrp.plugin.database.sync.repository.KeyedRepository
import com.fablesfantasyrp.plugin.database.sync.repository.MutableRepository
import java.lang.ref.SoftReference
import kotlin.concurrent.withLock

open class MassivelyCachingEntityRepository<K, T: Identifiable<K>, C>(child: C) : SimpleEntityRepository<K, T, C>(child)
		where C : KeyedRepository<K, T>,
			  C : MutableRepository<T>,
			  C: HasDirtyMarker<T> {
	override fun init() {
		super.init()

		// Important we don't call our own implementation of all()
		super.all().forEach { this.markStrong(it) }
	}

	override fun create(v: T): T {
		val result = child.create(v)
		lock.writeLock().withLock {
			cache[result.id] = SoftReference(result)
		}
		this.markStrong(result)
		return result
	}

	override fun all(): Collection<T> = cache.values.mapNotNull { it.get() }
	override fun allIds(): Collection<K> = cache.values.asSequence()
		.mapNotNull { it.get() }
		.map { it.id }
		.toList()
}
