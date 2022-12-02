package com.fablesfantasyrp.plugin.characters.command.provider

import com.fablesfantasyrp.plugin.characters.currentPlayerCharacter
import com.fablesfantasyrp.plugin.characters.playerCharacters
import com.fablesfantasyrp.plugin.utils.quoteCommandArgument
import com.sk89q.intake.argument.ArgumentParseException
import com.sk89q.intake.argument.CommandArgs
import com.sk89q.intake.argument.Namespace
import com.sk89q.intake.parametric.Provider
import org.bukkit.Server
import org.bukkit.entity.Player

class AllowCharacterNamePlayerProvider(private val server: Server,
									   private val playerProvider: Provider<Player>) : Provider<Player> {

	override fun isProvided(): Boolean = false

	private fun getByPlayerCharacter(arguments: CommandArgs, modifiers: List<Annotation>): Player? {
		return server.onlinePlayers.find { it.currentPlayerCharacter?.name == arguments.peek() }
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
		return server.playerCharacters.asSequence()
				.filter { it.player.isOnline && it.player.player?.currentPlayerCharacter == it }
				.map { it.name }
				.plus(playerProvider.getSuggestions(prefix, locals, modifiers))
				.filter { it.startsWith(prefix.removePrefix("\""), true) }
				.map { quoteCommandArgument(it) }
				.toList()
	}
}
