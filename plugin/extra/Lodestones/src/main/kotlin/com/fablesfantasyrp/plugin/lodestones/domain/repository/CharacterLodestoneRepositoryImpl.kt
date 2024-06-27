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
package com.fablesfantasyrp.plugin.lodestones.domain.repository

import com.fablesfantasyrp.plugin.characters.domain.entity.Character
import com.fablesfantasyrp.plugin.lodestones.domain.entity.Lodestone
import com.fablesfantasyrp.plugin.lodestones.domain.mapper.CharacterLodestoneMapper
import java.util.*

class CharacterLodestoneRepositoryImpl(private val mapper: CharacterLodestoneMapper, private val lodestones: LodestoneRepository) : CharacterLodestoneRepository {
	private val byCharacter = WeakHashMap<Character, HashSet<Lodestone>>()
	private val strongCache = HashSet<Character>()

	fun init() {
		lodestones.onDestroy { lodestone ->
			byCharacter.values.forEach { it.remove(lodestone) }
			mapper.destroy(lodestone)
		}
	}

	override fun forCharacter(character: Character): Set<Lodestone> {
		return this.forCharacterMutable(character)
	}

	override fun add(character: Character, lodestone: Lodestone) {
		this.forCharacterMutable(character).add(lodestone)
		strongCache.add(character)
	}

	override fun remove(character: Character, lodestone: Lodestone) {
		this.forCharacterMutable(character).remove(lodestone)
		strongCache.add(character)
	}

	private fun forCharacterMutable(character: Character): MutableSet<Lodestone> {
		return byCharacter.computeIfAbsent(character) { mapper.forCharacter(character).toHashSet() }
	}

	fun saveAllDirty() {
		for (character in strongCache) {
			val lodestones = byCharacter[character]!!
			val persistedLodestones = mapper.forCharacter(character)

			val added = lodestones.subtract(persistedLodestones)
			val removed = persistedLodestones.subtract(lodestones)

			added.forEach { mapper.add(character, it) }
			removed.forEach { mapper.remove(character, it) }
		}
	}
}
