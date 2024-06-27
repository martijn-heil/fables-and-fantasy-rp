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
package com.fablesfantasyrp.plugin.database.sync.repository.base

import com.fablesfantasyrp.plugin.database.entity.HasDestroyHandler
import com.fablesfantasyrp.plugin.database.model.Identifiable
import com.fablesfantasyrp.plugin.database.sync.repository.KeyedRepository
import com.fablesfantasyrp.plugin.database.sync.repository.MutableRepository

abstract class MappingRepository<KeyType, ChildType, ThisType, ChildRepositoryType>(private val child: ChildRepositoryType)
	: MutableRepository<ThisType>, KeyedRepository<KeyType, ThisType>, HasDestroyHandler<ThisType>
		where 	ThisType : Identifiable<KeyType>,
				ChildType : Identifiable<KeyType>,
				ChildRepositoryType: MutableRepository<ChildType>,
				ChildRepositoryType: KeyedRepository<KeyType, ChildType> {
	private val destroyHandlers = ArrayList<(ThisType) -> Unit>()

	abstract fun convertFromChild(v: ChildType): ThisType
	abstract fun convertToChild(v: ThisType): ChildType
	override fun create(v: ThisType): ThisType = convertFromChild(child.create(convertToChild(v)))
	override fun update(v: ThisType) = child.update(convertToChild(v))
	override fun allIds(): Collection<KeyType> = child.allIds()
	override fun all(): Collection<ThisType> = child.all().map { convertFromChild(it) }
	override fun forId(id: KeyType): ThisType? = child.forId(id)?.let { convertFromChild(it) }
	override fun createOrUpdate(v: ThisType): ThisType = convertFromChild(child.createOrUpdate(convertToChild(v)))

	override fun destroy(v: ThisType) {
		destroyHandlers.forEach { it(v) }
		child.destroy(convertToChild(v))
	}

	override fun onDestroy(callback: (ThisType) -> Unit) {
		destroyHandlers.add(callback)
	}
}
