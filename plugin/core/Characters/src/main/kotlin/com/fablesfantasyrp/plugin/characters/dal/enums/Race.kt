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
package com.fablesfantasyrp.plugin.characters.dal.enums

import com.fablesfantasyrp.plugin.characters.domain.CharacterStatsModifier

private const val BASE_MINIMUM_AGE = 8

enum class Race(private val displayName: String,
				val boosters: CharacterStatsModifier,
				val medianAge: Int? = null,
				val minimumAge: Int = BASE_MINIMUM_AGE,
				val creatureSize: CreatureSize = CreatureSize.MEDIUM) {

	ATTIAN_HUMAN(
		displayName = "Attian Human",
		boosters = CharacterStatsModifier(strength = 1, defense = 1, agility = 1, intelligence = 1),
		medianAge = 80,
		minimumAge = BASE_MINIMUM_AGE
	),

	HINTERLANDER_HUMAN(
		displayName = "Hinterlander Human",
		boosters = CharacterStatsModifier(strength = 1, defense = 2, intelligence = 1),
		medianAge = 80,
	),

	KHADAN_HUMAN(
		displayName = "Khadan Human",
		boosters = CharacterStatsModifier(strength = 2, agility = 2),
		medianAge = 80,
	),

	HIGH_ELF(
		displayName = "High Elf",
		boosters = CharacterStatsModifier(intelligence = 4),
		medianAge = 450,
		minimumAge = BASE_MINIMUM_AGE*4
	),

	DARK_ELF(
		displayName = "Dark Elf",
		boosters = CharacterStatsModifier(strength = 2, intelligence = 2),
		medianAge = 450,
		minimumAge = BASE_MINIMUM_AGE*4
	),

	WOOD_ELF(
		displayName = "Wood Elf",
		boosters = CharacterStatsModifier(strength = 1, agility = 3),
		medianAge = 350,
		minimumAge = BASE_MINIMUM_AGE*4
	),

	DWARF(
		displayName = "Dwarf",
		boosters = CharacterStatsModifier(defense = 4),
		medianAge = 200,
		creatureSize = CreatureSize.SMALL
	),

	TIEFLING(
		displayName = "Tiefling",
		boosters = CharacterStatsModifier(strength = 1, intelligence = 3),
		medianAge = 120,
	),

	ORC(
		displayName = "Orc",
		boosters = CharacterStatsModifier(strength = 3, defense = 1),
		medianAge = 150,
		creatureSize = CreatureSize.LARGE),

	GOBLIN(
		displayName = "Goblin",
		boosters = CharacterStatsModifier(defense = 1, intelligence = 1, agility = 2),
		medianAge = 130,
		creatureSize = CreatureSize.SMALL
	),

	HALFLING(
		displayName = "Halfling",
		boosters = CharacterStatsModifier(defense = 1, agility = 3),
		medianAge = 80,
		creatureSize = CreatureSize.SMALL
	),

	SYLVANI(
		displayName = "Sylvani",
		boosters = CharacterStatsModifier( agility = 2, intelligence = 4),
		minimumAge = BASE_MINIMUM_AGE*4
	),

	OTHER(
		displayName = "Other",
		boosters = CharacterStatsModifier(defense = 0, agility = 0, intelligence = 0),
	),

	@Deprecated("legacy race")
	HUMAN("Human", CharacterStatsModifier(strength = 1, defense = 1, agility = 1), 65);

	override fun toString() = displayName
}
