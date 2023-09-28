package com.fablesfantasyrp.plugin.characters.command.provider

import com.fablesfantasyrp.plugin.characters.domain.repository.CharacterRepository
import com.fablesfantasyrp.plugin.profile.ProfileManager
import com.fablesfantasyrp.plugin.utils.quoteCommandArgument
import com.sk89q.intake.argument.ArgumentParseException
import com.sk89q.intake.argument.CommandArgs
import com.sk89q.intake.argument.Namespace
import com.sk89q.intake.parametric.Provider
import org.bukkit.Server
import org.bukkit.entity.Player

class AllowCharacterNamePlayerProvider(private val server: Server,
									   private val playerProvider: Provider<Player>,
									   private val profileManager: ProfileManager,
									   private val characters: CharacterRepository) : Provider<Player> {

	override fun isProvided(): Boolean = false

	private fun getByPlayerCharacter(arguments: CommandArgs, modifiers: List<Annotation>): Player? {
		val character = characters.forName(arguments.peek()) ?: return null
		return profileManager.getCurrentForProfile(character.profile)
	}

	private fun getByPlayerProvider(arguments: CommandArgs, modifiers: List<Annotation>): Player? {
		return playerProvider.get(arguments, modifiers)
	}

	override fun get(arguments: CommandArgs, modifiers: List<Annotation>): Player {
		val firstAttempt = getByPlayerCharacter(arguments, modifiers)
		return if (firstAttempt != null) {
			arguments.next()
			firstAttempt
		} else {
			val secondAttempt = getByPlayerProvider(arguments, modifiers)
			secondAttempt ?: throw ArgumentParseException("Player not found")
		}
	}

	override fun getSuggestions(prefix: String, locals: Namespace, modifiers: List<Annotation>): List<String> {
		return server.onlinePlayers.asSequence()
				.mapNotNull { profileManager.getCurrentForPlayer(it)?.let { characters.forProfile(it) } }
				.map { it.name }
				.plus(playerProvider.getSuggestions(prefix, locals, modifiers))
				.filter { it.startsWith(prefix.removePrefix("\""), true) }
				.map { quoteCommandArgument(it) }
				.toList()
	}
}
