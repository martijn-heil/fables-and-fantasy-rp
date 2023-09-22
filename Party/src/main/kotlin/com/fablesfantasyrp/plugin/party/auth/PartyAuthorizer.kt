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
