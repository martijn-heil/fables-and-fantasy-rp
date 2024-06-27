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
package com.fablesfantasyrp.plugin.lodestones.domain.mapper

import com.fablesfantasyrp.plugin.characters.domain.entity.Character
import com.fablesfantasyrp.plugin.lodestones.dal.repository.CharacterLodestoneDataRepository
import com.fablesfantasyrp.plugin.lodestones.domain.entity.Lodestone
import com.fablesfantasyrp.plugin.lodestones.domain.repository.CharacterLodestoneRepository
import com.fablesfantasyrp.plugin.lodestones.domain.repository.LodestoneRepository

class CharacterLodestoneMapper(private val lodestones: LodestoneRepository, private val child: CharacterLodestoneDataRepository) : CharacterLodestoneRepository {
	override fun forCharacter(character: Character): Set<Lodestone> = lodestones.forIds(child.forCharacter(character.id).asSequence()).toSet()
	override fun add(character: Character, lodestone: Lodestone) = child.add(character.id, lodestone.id)
	override fun remove(character: Character, lodestone: Lodestone) = child.remove(character.id, lodestone.id)
	fun destroy(lodestone: Lodestone) = child.destroy(lodestone.id)
}
