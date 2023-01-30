package com.fablesfantasyrp.plugin.characters.command.provider

import com.fablesfantasyrp.plugin.characters.data.entity.EntityCharacterRepository
import com.fablesfantasyrp.plugin.profile.ProfileManager
import com.fablesfantasyrp.plugin.profile.data.entity.Profile
import com.fablesfantasyrp.plugin.utils.quoteCommandArgument
import com.gitlab.martijn_heil.nincommands.common.CommandTarget
import com.sk89q.intake.argument.ArgumentParseException
import com.sk89q.intake.argument.CommandArgs
import com.sk89q.intake.argument.Namespace
import com.sk89q.intake.parametric.Provider
import org.bukkit.Server
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.permissions.Permissible

class AllowCharacterNameProfileProvider(private val server: Server,
										private val characterRepository: EntityCharacterRepository,
										private val profileProvider: Provider<Profile>,
										private val profileManager: ProfileManager) : Provider<Profile> {

	override fun isProvided(): Boolean = false

	private fun getByPlayerCharacter(arguments: CommandArgs, modifiers: List<Annotation>): Profile? {
		return characterRepository.forName(arguments.peek())?.profile
	}

	private fun getByProfileProvider(arguments: CommandArgs, modifiers: List<Annotation>): Profile? {
		return profileProvider.get(arguments, modifiers)
	}

	override fun get(arguments: CommandArgs, modifiers: List<Annotation>): Profile? {
		val sender = arguments.namespace.get("sender") as CommandSender
		val permissible = arguments.namespace.get(Permissible::class.java)!!
		val targetAnnotation = modifiers.find { it is CommandTarget } as? CommandTarget

		if (arguments.hasNext()) {
			val firstAttempt = getByPlayerCharacter(arguments, modifiers)
			val result = if (firstAttempt != null) {
				arguments.next()
				firstAttempt
			} else {
				val secondAttempt = getByProfileProvider(arguments, modifiers)
				secondAttempt ?: throw ArgumentParseException("Profile not found")
			}

			if (targetAnnotation != null &&
					targetAnnotation.value.isNotBlank() &&
					result.owner != sender &&
					!permissible.hasPermission(targetAnnotation.value)) {
				throw ArgumentParseException("You need " + targetAnnotation.value)
			}

			return result
		} else if (targetAnnotation != null && sender is Player) {
			val ownProfile = profileManager.getCurrentForPlayer(sender)
			if (ownProfile == null) arguments.next() // Generate MissingArgumentException
			return ownProfile
		} else {
			arguments.next() // Generate MissingArgumentException
		}
		return null
	}

	override fun getSuggestions(prefix: String, locals: Namespace, modifiers: List<Annotation>): List<String> {
		return characterRepository.allNames()
				.filter { it.startsWith(prefix.removePrefix("\""), true) }
				.map { quoteCommandArgument(it) }
	}
}
