package com.fablesfantasyrp.plugin.characters.command.provider

import com.fablesfantasyrp.plugin.characters.PlayerCharacter
import com.fablesfantasyrp.plugin.characters.playerCharacters
import com.sk89q.intake.argument.ArgumentParseException
import com.sk89q.intake.argument.CommandArgs
import com.sk89q.intake.argument.Namespace
import com.sk89q.intake.parametric.Provider
import org.bukkit.Server

class PlayerCharacterProvider(private val server: Server) : Provider<PlayerCharacter> {
	override fun isProvided(): Boolean = false

	override fun get(arguments: CommandArgs, modifiers: List<Annotation>): PlayerCharacter {
		return server.playerCharacters.asSequence().filter { it.name == arguments.next() }.firstOrNull()
				?: throw ArgumentParseException("Character not found")
	}

	override fun getSuggestions(prefix: String, locals: Namespace, modifiers: List<Annotation>): List<String> {
		return server.playerCharacters.asSequence()
				.map { it.name }
				.filter { it.startsWith(prefix.removePrefix("\""), true) }
				.map { quote(it) }
				.toList()
	}
}
