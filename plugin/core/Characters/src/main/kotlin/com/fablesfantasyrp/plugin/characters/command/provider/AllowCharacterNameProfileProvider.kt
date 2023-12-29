package com.fablesfantasyrp.plugin.characters.command.provider

import com.fablesfantasyrp.plugin.characters.domain.repository.CharacterRepository
import com.fablesfantasyrp.plugin.characters.frunBlocking
import com.fablesfantasyrp.plugin.profile.ProfileManager
import com.fablesfantasyrp.plugin.profile.command.annotation.AllowPlayerName
import com.fablesfantasyrp.plugin.profile.data.entity.Profile
import com.fablesfantasyrp.plugin.utils.quoteCommandArgument
import com.fablesfantasyrp.caturix.spigot.common.CommandTarget
import com.fablesfantasyrp.caturix.argument.ArgumentParseException
import com.fablesfantasyrp.caturix.argument.CommandArgs
import com.fablesfantasyrp.caturix.argument.Namespace
import com.fablesfantasyrp.caturix.parametric.Provider
import org.bukkit.Server
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.permissions.Permissible
import org.bukkit.plugin.Plugin

class AllowCharacterNameProfileProvider(private val server: Server,
										private val characterRepository: CharacterRepository,
										private val profileProvider: Provider<Profile>,
										private val profileManager: ProfileManager) : Provider<Profile> {
	override val isProvided: Boolean = false

	private fun getByPlayerCharacter(arguments: CommandArgs, modifiers: List<Annotation>): Profile? {
		return frunBlocking { characterRepository.forName(arguments.peek())?.profile }
	}

	private suspend fun getByProfileProvider(arguments: CommandArgs, modifiers: List<Annotation>): Profile? {
		return profileProvider.get(arguments, modifiers)
	}

	override suspend fun get(arguments: CommandArgs, modifiers: List<Annotation>): Profile {
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
			return ownProfile!!
		} else {
			arguments.next() // Generate MissingArgumentException
			throw IllegalStateException()
		}
	}

	override suspend fun getSuggestions(prefix: String, locals: Namespace, modifiers: List<Annotation>): List<String> {
		val allowPlayerName = modifiers.any { it is AllowPlayerName }

		return frunBlocking {
			characterRepository.allNames()
				.asSequence()
				.filter { it.startsWith(prefix.removePrefix("\""), true) }
				.map { quoteCommandArgument(it) }
				.plus(if (allowPlayerName) server.onlinePlayers.map { it.name }.filter { it.lowercase().startsWith(prefix.lowercase()) } else emptyList())
				.distinct()
				.toList()
		}
	}
}
