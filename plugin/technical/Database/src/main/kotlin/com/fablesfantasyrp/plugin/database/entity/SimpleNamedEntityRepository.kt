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

open class SimpleNamedEntityRepository<K, T, C>(child: C) : SimpleEntityRepository<K, T, C>(child), NamedRepository<T>
	where T : Identifiable<K>,
		  T : Named,
		  C : KeyedRepository<K, T>,
		  C : MutableRepository<T>,
		  C : HasDirtyMarker<T>,
		  C : NamedRepository<T> {
    private val byName = HashMap<String, K>()

	override fun init() {
		super.init()
		this.all().forEach { byName[it.name] = it.id }
	}

	override fun forName(name: String): T? = byName[name]?.let { forId(it) }
	override fun allNames(): Set<String> = byName.keys

	override fun markDirty(v: T, what: String, oldValue: Any?, newValue: Any?) {
		super.markDirty(v, what, oldValue, newValue)
		if (what == "name") {
			check(oldValue != null && newValue != null)
			byName.remove(oldValue as String)
			byName[newValue as String] = v.id
		}
	}

	override fun create(v: T): T {
		require(!nameExists(v.name))
		val created = super.create(v)
		byName[created.name] = created.id
		return created
	}

	override fun destroy(v: T) {
		super.destroy(v)
		byName.remove(v.name)
	}
}
