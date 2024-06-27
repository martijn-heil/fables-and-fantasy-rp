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
package com.fablesfantasyrp.plugin.characters.dal.model

import com.fablesfantasyrp.plugin.characters.dal.enums.CharacterStatKind
import com.fablesfantasyrp.plugin.characters.dal.enums.Gender
import com.fablesfantasyrp.plugin.characters.dal.enums.Race
import com.fablesfantasyrp.plugin.characters.domain.CharacterStats
import com.fablesfantasyrp.plugin.characters.domain.CharacterTrait
import com.fablesfantasyrp.plugin.database.model.Identifiable
import com.fablesfantasyrp.plugin.time.javatime.FablesLocalDate
import java.time.Instant

data class CharacterData(
	override val id: Int,
	val name: String,
	val description: String,
	val gender: Gender,
	val race: Race,
	val stats: CharacterStats,
	val lastSeen: Instant? = null,
	val createdAt: Instant? = Instant.now(),
	val dateOfBirth: FablesLocalDate?,
	val dateOfNaturalDeath: FablesLocalDate?,
	val isDead: Boolean = false,
	val diedAt: Instant? = null,
	val isShelved: Boolean = false,
	val shelvedAt: Instant? = null,
	val changedStatsAt: Instant? = null,
	val traits: Set<CharacterTrait>) : Identifiable<Int> {

	val maximumHealth: UInt get() = (12 + CharacterStatKind.STRENGTH.getRollModifierFor(stats.strength)).toUInt()
}
