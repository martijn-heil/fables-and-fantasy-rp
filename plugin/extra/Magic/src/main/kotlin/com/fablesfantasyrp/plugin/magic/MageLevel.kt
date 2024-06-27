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
package com.fablesfantasyrp.plugin.magic

import com.fablesfantasyrp.plugin.characters.domain.CharacterTrait
import com.fablesfantasyrp.plugin.characters.domain.repository.CharacterRepository
import com.fablesfantasyrp.plugin.magic.domain.entity.Mage
import com.fablesfantasyrp.plugin.utils.Services
import org.koin.core.context.GlobalContext

val MAGE_LEVEL_MAP = mapOf(
		Pair(1, mapOf( // Level 1
				Pair(1, 2) // 2 level 1 spells
		)),
		Pair(2, mapOf( // Level 2
				Pair(1, 3) // 3 level 1 spells
		)),
		Pair(3, mapOf( // Level 3
				Pair(1, 4), // 4 level 1 spells
				Pair(2, 2)  // 2 level 2 spells
		)),
		Pair(4, mapOf( // Level 4
				Pair(1, 4), // 4 level 1 spells
				Pair(2, 3)  // 3 level 1 spells
		)),
		Pair(5, mapOf( // Level 5
				Pair(1, 4), // 4 level 1 spells
				Pair(2, 3), // 3 level 2 spells
				Pair(3, 2)  // 2 level 3 spells
		)),
		Pair(6, mapOf( // Level 6
				Pair(1, 4), // 4 level 1 spells
				Pair(2, 3), // 3 level 2 spells
				Pair(3, 3)  // 2 level 3 spells
		)),
		Pair(7, mapOf( // Level 7
				Pair(1, 4), // 4 level 1 spells
				Pair(2, 3), // 3 level 2 spells
				Pair(3, 3), // 2 level 3 spells
				Pair(4, 1)  // 1 level 4 spell
		)),
		Pair(8, mapOf( // Level 8
				Pair(1, 4), // 4 level 1 spells
				Pair(2, 3), // 3 level 2 spells
				Pair(3, 3), // 2 level 3 spells
				Pair(4, 2)  // 2 level 4 spells
		)),
		Pair(9, mapOf( // Level 9
				Pair(1, 4), // 4 level 1 spells
				Pair(2, 3), // 3 level 2 spells
				Pair(3, 3), // 2 level 3 spells
				Pair(4, 3), // 3 level 4 spells
				Pair(5, 1)  // 1 level 5 spell
		)),
		Pair(10, mapOf( // Level 10
				Pair(1, 4), // 4 level 1 spells
				Pair(2, 3), // 3 level 2 spells
				Pair(3, 3), // 2 level 3 spells
				Pair(4, 3), // 3 level 4 spells
				Pair(5, 2)  // 2 level 5 spells
		))
)

fun getMaxSpells(mageLevel: Int, spellLevel: Int, mage: Mage?): Int {
	var n = MAGE_LEVEL_MAP[mageLevel]?.get(spellLevel) ?: 0

	if (mage != null && mage.character.traits.contains(CharacterTrait.KNOWLEDGEABLE)) {
		n += when (spellLevel) {
			1 -> 2
			2 -> 1
			else -> 0
		}
	}

	return n
}

fun getRequiredMageLevel(spellLevel: Int, nthSpell: Int, mage: Mage?): Int? = MAGE_LEVEL_MAP.entries
			.sortedBy { mageLevel -> mageLevel.key }
			.find { mageLevel -> getMaxSpells(mageLevel.key, spellLevel, mage) >= nthSpell }?.key
