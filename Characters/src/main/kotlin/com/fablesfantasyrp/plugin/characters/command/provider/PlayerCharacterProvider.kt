package com.fablesfantasyrp.plugin.characters.command.provider

import com.fablesfantasyrp.plugin.characters.currentPlayerCharacter
import com.fablesfantasyrp.plugin.characters.data.PlayerCharacterData
import com.fablesfantasyrp.plugin.characters.playerCharacters
import com.gitlab.martijn_heil.nincommands.common.CommandTarget
import com.sk89q.intake.argument.ArgumentParseException
import com.sk89q.intake.argument.CommandArgs
import com.sk89q.intake.argument.Namespace
import com.sk89q.intake.parametric.Provider
import org.bukkit.Server
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class PlayerCharacterProvider(private val server: Server) : Provider<PlayerCharacterData> {
	override fun isProvided(): Boolean = false

	override fun get(arguments: CommandArgs, modifiers: List<Annotation>): PlayerCharacterData? {
		val sender = arguments.namespace.get("sender") as CommandSender
		val targetAnnotation = modifiers.find { it is CommandTarget }

		if (arguments.hasNext()) {
			val name = arguments.next()
			return server.playerCharacters.asSequence().filter { it.name == name }.firstOrNull()
					?: throw ArgumentParseException("A character called '$name' could not be found")
		} else if (targetAnnotation != null && sender is Player && sender.currentPlayerCharacter != null) {
			return sender.currentPlayerCharacter!!
		} else {
			// Generate MissingArgumentException
			arguments.next()
		}
		return null
	}

	override fun getSuggestions(prefix: String, locals: Namespace, modifiers: List<Annotation>): List<String> {
		return server.playerCharacters.asSequence()
				.map { it.name }
				.filter { it.startsWith(prefix.removePrefix("\""), true) }
				.map { quote(it) }
				.toList()
	}
}
