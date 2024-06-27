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
