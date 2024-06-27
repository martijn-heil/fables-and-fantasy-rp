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
import com.fablesfantasyrp.plugin.database.async.repository.AsyncMutableRepository
import com.fablesfantasyrp.plugin.database.entity.HasDestroyHandler
import com.fablesfantasyrp.plugin.database.model.Identifiable
import com.fablesfantasyrp.plugin.database.sync.repository.KeyedRepository
import com.fablesfantasyrp.plugin.database.sync.repository.MutableRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

abstract class AsyncMappingRepository<KeyType, ChildType, ThisType, ChildRepositoryType>(child: ChildRepositoryType)
	: AsyncMappingImmutableRepository<KeyType, ChildType, ThisType, ChildRepositoryType>(child),
	AsyncMutableRepository<ThisType>, AsyncKeyedRepository<KeyType, ThisType>, HasDestroyHandler<ThisType>
		where 	ThisType : Identifiable<KeyType>,
				ChildType : Identifiable<KeyType>,
				ChildRepositoryType: MutableRepository<ChildType>,
				ChildRepositoryType: KeyedRepository<KeyType, ChildType> {
	private val destroyHandlers = ArrayList<(ThisType) -> Unit>()

	abstract fun convertToChild(v: ThisType): ChildType

	override suspend fun create(v: ThisType): ThisType
		= convertToChild(v)
			.let { withContext(Dispatchers.IO) { child.create(it) } }
			.let { convertFromChild(it) }

	override suspend fun update(v: ThisType)
		= convertToChild(v)
			.let { withContext(Dispatchers.IO) { child.update(it) } }

	override suspend fun createOrUpdate(v: ThisType): ThisType
		= convertToChild(v)
			.let { withContext(Dispatchers.IO) { child.createOrUpdate(it) } }
			.let { convertFromChild(it) }

	override suspend fun destroy(v: ThisType) {
		destroyHandlers.forEach { it(v) }
		convertToChild(v)
			.let { withContext(Dispatchers.IO) { child.destroy(it) } }
	}

	override fun onDestroy(callback: (ThisType) -> Unit) {
		destroyHandlers.add(callback)
	}
}
