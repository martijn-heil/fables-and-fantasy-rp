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
package com.fablesfantasyrp.plugin.database.sync.repository

import com.fablesfantasyrp.plugin.database.model.Identifiable

interface KeyedRepository<K, T: Identifiable<K>> : Repository<T> {
	fun forId(id: K): T?
	fun forIds(ids: Sequence<K>): Collection<T> = ids.mapNotNull { forId(it) }.toList()
	fun allIds(): Collection<K>
	fun exists(id: K): Boolean = forId(id) != null
}
