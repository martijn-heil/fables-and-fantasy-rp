package com.fablesfantasyrp.plugin.party.auth

import com.fablesfantasyrp.plugin.characters.domain.entity.Character
import com.fablesfantasyrp.plugin.characters.domain.repository.CharacterRepository
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
		val whoCharacter = (who as? Player)?.let { profileManager.getCurrentForPlayer(it)?.let { characters.forProfile(it) } }
		return (whoCharacter != null && party.owner == whoCharacter) || who.hasPermission(Permission.Admin)
	}

	private fun isAdmin(party: Party, who: Character): Boolean {
		val whoPlayer = profileManager.getCurrentForProfile(who.profile)
		return (party.owner == who) || (whoPlayer != null && whoPlayer.hasPermission(Permission.Admin))
	}
}
