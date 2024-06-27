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
package com.fablesfantasyrp.plugin.magic.domain.repository

import com.fablesfantasyrp.plugin.characters.domain.entity.Character
import com.fablesfantasyrp.plugin.database.entity.MassivelyCachingEntityRepository
import com.fablesfantasyrp.plugin.magic.dal.enums.MagicPath
import com.fablesfantasyrp.plugin.magic.domain.entity.Mage
import com.fablesfantasyrp.plugin.magic.domain.mapper.repository.MageRepositoryMapper

class MageRepositoryImpl(child: MageRepositoryMapper) : MassivelyCachingEntityRepository<Long, Mage, MageRepositoryMapper>(child), MageRepository {

	override fun forCharacterOrCreate(c: Character): Mage {
		val maybe = this.forCharacter(c)
		return if (maybe != null) {
			maybe
		} else {
			val obj = Mage(
					character = c,
					magicPath = MagicPath.AEROMANCY,
					magicLevel = 0,
					spells = emptyList()
			)
			val result = this.create(obj)
			result.dirtyMarker = this
			result
		}
	}

	override fun forCharacter(c: Character): Mage? = this.forId(c.id.toLong())

	override fun destroy(v: Mage) {
		super.destroy(v)
		v.isDeleted = true
	}
}
