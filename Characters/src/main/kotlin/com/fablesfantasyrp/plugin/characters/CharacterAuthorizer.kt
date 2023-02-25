package com.fablesfantasyrp.plugin.characters

import com.fablesfantasyrp.plugin.characters.data.entity.Character
import com.fablesfantasyrp.plugin.profile.data.entity.Profile
import com.fablesfantasyrp.plugin.utils.AuthorizationResult
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

interface CharacterAuthorizer {
	fun mayEdit(who: CommandSender, what: Character, allowShelved: Boolean = false): AuthorizationResult
	fun mayBecome(who: Player, what: Character, instant: Boolean = false, force: Boolean = false): AuthorizationResult
	fun mayBecome(who: Player, what: Profile, instant: Boolean = false, force: Boolean = false): AuthorizationResult
}
