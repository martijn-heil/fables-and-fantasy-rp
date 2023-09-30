package com.fablesfantasyrp.plugin.characters

import com.fablesfantasyrp.plugin.characters.domain.entity.Character
import com.fablesfantasyrp.plugin.characters.domain.repository.CharacterRepository
import com.fablesfantasyrp.plugin.profile.data.entity.Profile
import com.fablesfantasyrp.plugin.utils.AuthorizationResult
import com.fablesfantasyrp.plugin.utils.FABLES_ADMIN
import org.bukkit.entity.Player
import org.bukkit.permissions.Permissible

class CharacterAuthorizerImpl(private val characters: CharacterRepository) : CharacterAuthorizer {
	override fun mayEdit(who: Permissible, what: Character, allowShelved: Boolean): AuthorizationResult {
		if (!allowShelved && what.isShelved) {
			return AuthorizationResult(false, "This character is shelved")
		}

		if (what.isDead) {
			return AuthorizationResult(false, "This character is dead")
		}

		val owner = what.profile.owner

		if (who != owner &&
			!(who.hasPermission(Permission.Any) ||
				(who.hasPermission(Permission.Staff) && what.isStaffCharacter))) {
			return AuthorizationResult(false)
		}

		return AuthorizationResult(true)
	}

	override fun mayBecome(who: Player, what: Character, instant: Boolean, force: Boolean): AuthorizationResult {
		return mayBecome(who, what.profile, instant, force)
	}

	override fun mayBecome(who: Player, what: Profile, instant: Boolean, force: Boolean): AuthorizationResult {
		val owner = what.owner
		val whatCharacter = characters.forProfile(what)

		if (force && !who.hasPermission(Permission.Command.Characters.Become + ".force")) {
			return AuthorizationResult(false, "Permission denied")
		}

		if (instant && !who.hasPermission(Permission.Command.Characters.Become + ".instant")) {
			return AuthorizationResult(false, "Permission denied")
		}

		if (what.owner == FABLES_ADMIN) {
			if (!who.hasPermission(Permission.Staff)) {
				return AuthorizationResult(false, "You do not have permission to become a staff character.")
			}
		} else if (owner != who && !who.hasPermission(Permission.Any)) {
			return AuthorizationResult(false, "You do not have permission to become a character that you don't own.")
		}

		if (whatCharacter?.isShelved == true) {
			return AuthorizationResult(false, "This character is currently shelved")
		}

		if (whatCharacter?.isDead == true) {
			return AuthorizationResult(false, "This character is dead.")
		}

		return AuthorizationResult(true)
	}

	override fun mayEditRace(who: Permissible, what: Character, allowShelved: Boolean): AuthorizationResult
		= mayEditProperty(who, what, allowShelved, Permission.Change.Race)

	override fun mayEditGender(who: Permissible, what: Character, allowShelved: Boolean): AuthorizationResult
		= mayEditProperty(who, what, allowShelved, Permission.Change.Gender)

	override fun mayEditDateOfBirth(who: Permissible, what: Character, allowShelved: Boolean): AuthorizationResult
		= mayEditProperty(who, what, allowShelved, Permission.Change.DateOfBirth)

	override fun mayEditName(who: Permissible, what: Character, allowShelved: Boolean): AuthorizationResult
		= mayEditProperty(who, what, allowShelved, Permission.Change.Name)

	override fun mayEditDescription(who: Permissible, what: Character, allowShelved: Boolean): AuthorizationResult
		= mayEditProperty(who, what, allowShelved, Permission.Change.Description)

	override fun mayEditStats(who: Permissible, what: Character, allowShelved: Boolean): AuthorizationResult
		= mayEditProperty(who, what, allowShelved, Permission.Change.Stats)

	private fun mayEditProperty(who: Permissible, what: Character, allowShelved: Boolean, permission: String): AuthorizationResult {
		mayEdit(who, what, allowShelved).orElse { return AuthorizationResult(false, it) }
		if (!what.isStaffCharacter && !who.hasPermission(permission)) {
			return AuthorizationResult(false)
		}

		return AuthorizationResult(true)
	}
}
