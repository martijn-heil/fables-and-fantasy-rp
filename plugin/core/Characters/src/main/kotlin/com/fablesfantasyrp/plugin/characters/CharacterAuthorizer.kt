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
package com.fablesfantasyrp.plugin.characters

import com.fablesfantasyrp.plugin.characters.domain.entity.Character
import com.fablesfantasyrp.plugin.profile.data.entity.Profile
import com.fablesfantasyrp.plugin.utils.AuthorizationResult
import org.bukkit.entity.Player
import org.bukkit.permissions.Permissible

interface CharacterAuthorizer {
	fun mayEdit(who: Permissible, what: Character, allowShelved: Boolean = false): AuthorizationResult
	fun mayEditRace(who: Permissible, what: Character, allowShelved: Boolean = false): AuthorizationResult
	fun mayEditGender(who: Permissible, what: Character, allowShelved: Boolean = false): AuthorizationResult
	fun mayEditDateOfBirth(who: Permissible, what: Character, allowShelved: Boolean = true): AuthorizationResult
	fun mayEditTraits(who: Permissible, what: Character, allowShelved: Boolean = true): AuthorizationResult
	fun mayEditName(who: Permissible, what: Character, allowShelved: Boolean = false): AuthorizationResult
	fun mayEditDescription(who: Permissible, what: Character, allowShelved: Boolean = false): AuthorizationResult
	fun mayEditStats(who: Permissible, what: Character, allowShelved: Boolean = false): AuthorizationResult
	fun mayTransfer(who: Permissible, what: Character): AuthorizationResult
	suspend fun mayBecome(who: Player, what: Character, instant: Boolean = false, force: Boolean = false): AuthorizationResult
	suspend fun mayBecome(who: Player, what: Profile, instant: Boolean = false, force: Boolean = false): AuthorizationResult
}
