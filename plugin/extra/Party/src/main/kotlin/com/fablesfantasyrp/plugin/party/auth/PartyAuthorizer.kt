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
package com.fablesfantasyrp.plugin.party.auth

import com.fablesfantasyrp.plugin.characters.domain.entity.Character
import com.fablesfantasyrp.plugin.party.data.entity.Party
import com.fablesfantasyrp.plugin.utils.AuthorizationResult
import org.bukkit.command.CommandSender

interface PartyAuthorizer {
	fun mayInviteMember(party: Party, who: Character): AuthorizationResult
	fun mayKickMember(party: Party, who: Character, target: Character): AuthorizationResult
	fun mayDisband(party: Party, who: Character): AuthorizationResult
	fun mayRename(party: Party, who: Character): AuthorizationResult
	fun mayTransfer(party: Party, who: Character): AuthorizationResult
	fun mayJoin(party: Party, who: Character): AuthorizationResult

	fun mayInviteMember(party: Party, who: CommandSender): AuthorizationResult
	fun mayKickMember(party: Party, who: CommandSender, target: Character):AuthorizationResult
	fun mayDisband(party: Party, who: CommandSender): AuthorizationResult
	fun mayRename(party: Party, who: CommandSender): AuthorizationResult
	fun mayTransfer(party: Party, who: CommandSender): AuthorizationResult
}
