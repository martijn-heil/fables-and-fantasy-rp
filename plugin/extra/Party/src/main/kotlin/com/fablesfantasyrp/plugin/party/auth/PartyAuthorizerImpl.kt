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
import com.fablesfantasyrp.plugin.characters.domain.repository.CharacterRepository
import com.fablesfantasyrp.plugin.party.frunBlocking
import com.fablesfantasyrp.plugin.party.Permission
import com.fablesfantasyrp.plugin.party.data.entity.Party
import com.fablesfantasyrp.plugin.profile.ProfileManager
import com.fablesfantasyrp.plugin.utils.AuthorizationResult
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class PartyAuthorizerImpl(private val profileManager: ProfileManager,
						  private val characters: CharacterRepository) : PartyAuthorizer {
	override fun mayInviteMember(party: Party, who: Character): AuthorizationResult {
		return if(isAdmin(party, who)) AuthorizationResult(true)
		else AuthorizationResult(false, "You are not authorised to invite a member")
	}

	override fun mayKickMember(party: Party, who: Character, target: Character): AuthorizationResult {
		return when {
			target == party.owner -> AuthorizationResult(false, "You cannot kick the owner of the party")
			isAdmin(party, who) -> AuthorizationResult(true)
			else -> AuthorizationResult(false, "You are not authorised to kick a member")
		}
	}

	override fun mayDisband(party: Party, who: Character): AuthorizationResult {
		return if(isAdmin(party, who)) AuthorizationResult(true)
		else AuthorizationResult(false, "You are not authorised to disband the party")
	}

	override fun mayRename(party: Party, who: Character): AuthorizationResult {
		return if(isAdmin(party, who)) AuthorizationResult(true)
		else AuthorizationResult(false, "You are not authorised to rename the party")
	}

	override fun mayTransfer(party: Party, who: Character): AuthorizationResult {
		return if(isAdmin(party, who)) AuthorizationResult(true)
		else AuthorizationResult(false, "You are not authorised to transfer the party")
	}

	override fun mayJoin(party: Party, who: Character): AuthorizationResult {
		return if (party.invites.contains(who) || isAdmin(party, who)) AuthorizationResult(true)
		else AuthorizationResult(false, "You are not authorised to join this party")
	}

	override fun mayInviteMember(party: Party, who: CommandSender): AuthorizationResult {
		return if(isAdmin(party, who)) AuthorizationResult(true)
		else AuthorizationResult(false, "You are not authorised to invite a member")
	}

	override fun mayKickMember(party: Party, who: CommandSender, target: Character): AuthorizationResult {
		return when {
			target == party.owner -> AuthorizationResult(false, "You cannot kick the owner of the party")
			isAdmin(party, who) -> AuthorizationResult(true)
			else -> AuthorizationResult(false, "You are not authorised to kick a member")
		}
	}

	override fun mayDisband(party: Party, who: CommandSender): AuthorizationResult {
		return if(isAdmin(party, who)) AuthorizationResult(true)
		else AuthorizationResult(false, "You are not authorised to disband the party")
	}

	override fun mayRename(party: Party, who: CommandSender): AuthorizationResult {
		return if(isAdmin(party, who)) AuthorizationResult(true)
		else AuthorizationResult(false, "You are not authorised to rename the party")
	}

	override fun mayTransfer(party: Party, who: CommandSender): AuthorizationResult {
		return if(isAdmin(party, who)) AuthorizationResult(true)
		else AuthorizationResult(false, "You are not authorised to transfer the party")
	}

	private fun isAdmin(party: Party, who: CommandSender): Boolean {
		return frunBlocking {
			val whoCharacter = (who as? Player)?.let { profileManager.getCurrentForPlayer(it)?.let { characters.forProfile(it) } }
			(whoCharacter != null && party.owner == whoCharacter) || who.hasPermission(Permission.Admin)
		}
	}

	private fun isAdmin(party: Party, who: Character): Boolean {
		val whoPlayer = profileManager.getCurrentForProfile(who.profile)
		return (party.owner == who) || (whoPlayer != null && whoPlayer.hasPermission(Permission.Admin))
	}
}
