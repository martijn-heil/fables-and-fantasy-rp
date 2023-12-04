package com.fablesfantasyrp.plugin.magic

import com.fablesfantasyrp.plugin.characters.domain.CharacterTrait
import com.fablesfantasyrp.plugin.magic.domain.entity.Mage

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
