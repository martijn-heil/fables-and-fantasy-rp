package com.fablesfantasyrp.plugin.characters.command.provider

import com.fablesfantasyrp.plugin.characters.domain.repository.CharacterRepository
import com.fablesfantasyrp.plugin.characters.frunBlocking
import com.fablesfantasyrp.plugin.profile.ProfileManager
import com.fablesfantasyrp.plugin.utils.quoteCommandArgument
import com.fablesfantasyrp.caturix.argument.ArgumentParseException
import com.fablesfantasyrp.caturix.argument.CommandArgs
import com.fablesfantasyrp.caturix.argument.Namespace
import com.fablesfantasyrp.caturix.parametric.Provider
import org.bukkit.Server
import org.bukkit.entity.Player

class AllowCharacterNamePlayerProvider(private val server: Server,
									   private val playerProvider: Provider<Player>,
									   private val profileManager: ProfileManager,
									   private val characters: CharacterRepository) : Provider<Player> {

	override val isProvided: Boolean = false

	private fun getByPlayerCharacter(arguments: CommandArgs, modifiers: List<Annotation>): Player? {
		val character = frunBlocking { characters.forName(arguments.peek()) } ?: return null
		return profileManager.getCurrentForProfile(character.profile)
	}

	private suspend fun getByPlayerProvider(arguments: CommandArgs, modifiers: List<Annotation>): Player? {
		return playerProvider.get(arguments, modifiers)
	}

	override suspend fun get(arguments: CommandArgs, modifiers: List<Annotation>): Player {
		val firstAttempt = getByPlayerCharacter(arguments, modifiers)
		return if (firstAttempt != null) {
			arguments.next()
			firstAttempt
		} else {
			val secondAttempt = getByPlayerProvider(arguments, modifiers)
			secondAttempt ?: throw ArgumentParseException("Player not found")
		}
	}

	override suspend fun getSuggestions(prefix: String, locals: Namespace, modifiers: List<Annotation>): List<String> {
		return server.onlinePlayers.asSequence()
				.mapNotNull { profileManager.getCurrentForPlayer(it)?.let { frunBlocking { characters.forProfile(it) } } }
				.map { it.name }
				.plus(playerProvider.getSuggestions(prefix, locals, modifiers))
				.filter { it.startsWith(prefix.removePrefix("\""), true) }
				.map { quoteCommandArgument(it) }
				.toList()
	}
}
