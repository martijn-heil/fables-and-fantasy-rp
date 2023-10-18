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
	fun mayEditAge(who: Permissible, what: Character, allowShelved: Boolean = false): AuthorizationResult
	fun mayEditName(who: Permissible, what: Character, allowShelved: Boolean = false): AuthorizationResult
	fun mayEditDescription(who: Permissible, what: Character, allowShelved: Boolean = false): AuthorizationResult
	fun mayEditStats(who: Permissible, what: Character, allowShelved: Boolean = false): AuthorizationResult
	fun mayTransfer(who: Permissible, what: Character): AuthorizationResult
	fun mayBecome(who: Player, what: Character, instant: Boolean = false, force: Boolean = false): AuthorizationResult
	fun mayBecome(who: Player, what: Profile, instant: Boolean = false, force: Boolean = false): AuthorizationResult
}
