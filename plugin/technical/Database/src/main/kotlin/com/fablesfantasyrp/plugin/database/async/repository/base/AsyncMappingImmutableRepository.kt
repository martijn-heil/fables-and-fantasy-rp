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
package com.fablesfantasyrp.plugin.database.async.repository.base

import com.fablesfantasyrp.plugin.database.async.repository.AsyncKeyedRepository
import com.fablesfantasyrp.plugin.database.model.Identifiable
import com.fablesfantasyrp.plugin.database.sync.repository.KeyedRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

abstract class AsyncMappingImmutableRepository<KeyType, ChildType, ThisType, ChildRepositoryType>(protected val child: ChildRepositoryType)
	: AsyncKeyedRepository<KeyType, ThisType>
		where 	ThisType : Identifiable<KeyType>,
				ChildType : Identifiable<KeyType>,
				ChildRepositoryType: KeyedRepository<KeyType, ChildType> {

	abstract fun convertFromChild(v: ChildType): ThisType

	override suspend fun allIds(): Collection<KeyType>
		= withContext(Dispatchers.IO) { child.allIds() }

	override suspend fun all(): Collection<ThisType>
		= withContext(Dispatchers.IO) { child.all() }
			.map { convertFromChild(it) }

	override suspend fun forId(id: KeyType): ThisType?
		= withContext(Dispatchers.IO) { child.forId(id) }
			?.let { convertFromChild(it) }
}
