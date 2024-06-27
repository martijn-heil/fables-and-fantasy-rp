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
package com.fablesfantasyrp.plugin.magic.domain.mapper.repository

import com.fablesfantasyrp.plugin.characters.domain.entity.Character
import com.fablesfantasyrp.plugin.characters.domain.repository.CharacterRepository
import com.fablesfantasyrp.plugin.database.sync.repository.base.MappingRepository
import com.fablesfantasyrp.plugin.database.repository.DirtyMarker
import com.fablesfantasyrp.plugin.database.model.HasDirtyMarker
import com.fablesfantasyrp.plugin.magic.dal.model.MageData
import com.fablesfantasyrp.plugin.magic.dal.repository.MageDataRepository
import com.fablesfantasyrp.plugin.magic.domain.entity.Mage
import com.fablesfantasyrp.plugin.magic.domain.repository.MageRepository
import com.fablesfantasyrp.plugin.magic.frunBlocking

class MageRepositoryMapper(private val child: MageDataRepository,
						   private val characters: CharacterRepository)
	: MappingRepository<Long, MageData, Mage, MageDataRepository>(child),
	MageRepository, HasDirtyMarker<Mage> {

	override var dirtyMarker: DirtyMarker<Mage>? = null

	override fun convertFromChild(v: MageData) = Mage(
		character = frunBlocking { characters.forId(v.id.toInt())!! },
		magicLevel = v.magicLevel,
		magicPath = v.magicPath,
		spells = v.spells,
		dirtyMarker = dirtyMarker
	)

	override fun convertToChild(v: Mage) = MageData(
		id = v.id,
		magicLevel = v.magicLevel,
		magicPath = v.magicPath,
		spells = v.spells
	)

	override fun forCharacter(c: Character): Mage? = child.forCharacter(c.id)?.let { convertFromChild(it) }
	override fun forCharacterOrCreate(c: Character): Mage = convertFromChild(child.forCharacterOrCreate(c.id))
}
