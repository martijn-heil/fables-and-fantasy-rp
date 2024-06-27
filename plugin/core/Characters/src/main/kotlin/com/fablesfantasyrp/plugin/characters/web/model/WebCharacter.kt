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
package com.fablesfantasyrp.plugin.characters.web.model

import com.fablesfantasyrp.plugin.characters.dal.enums.Gender
import com.fablesfantasyrp.plugin.characters.dal.enums.Race
import com.fablesfantasyrp.plugin.characters.domain.entity.Character
import kotlinx.serialization.Serializable

@Serializable
data class WebCharacter(val id: Int,
						val name: String,
						val race: Race,
						val age: UInt?,
						val gender: Gender,
						val description: String,
						val isDead: Boolean,
						val isShelved: Boolean,
						val lastSeen: Long?,
						val stats: WebCharacterStats,
						val totalStats: WebCharacterStats)
fun Character.transform() = WebCharacter(
	id = id,
	name = name,
	age = age,
	gender = gender,
	description = description,
	isDead = isDead,
	isShelved = isShelved,
	lastSeen = lastSeen?.epochSecond,
	race = race,
	stats = stats.transform(),
	totalStats = totalStats.transform()
)
