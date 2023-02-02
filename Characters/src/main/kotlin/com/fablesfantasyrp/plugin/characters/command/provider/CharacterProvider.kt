package com.fablesfantasyrp.plugin.characters.command.provider

import com.fablesfantasyrp.plugin.characters.currentPlayerCharacter
import com.fablesfantasyrp.plugin.characters.data.entity.Character
import com.fablesfantasyrp.plugin.characters.data.entity.EntityCharacterRepository
import com.fablesfantasyrp.plugin.characters.playerCharacters
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

class CharacterProvider(private val server: Server, private val characters: EntityCharacterRepository) : Provider<Character> {
	override fun isProvided(): Boolean = false

	override fun get(arguments: CommandArgs, modifiers: List<Annotation>): Character? {
		val sender = arguments.namespace.get("sender") as CommandSender
		val targetAnnotation = modifiers.find { it is CommandTarget } as? CommandTarget

		if (arguments.hasNext()) {
			val name = arguments.next()
			val permissible = arguments.namespace.get(Permissible::class.java)!!

			if (targetAnnotation != null && targetAnnotation.value.isNotBlank() && !permissible.hasPermission(targetAnnotation.value)) {
				throw ArgumentParseException("You need " + targetAnnotation.value)
			}

			return if (name.startsWith("#")) {
				val id = name.removePrefix("#").toIntOrNull() ?: throw ArgumentParseException("Invalid identifier")
				characters.forId(id)
			} else {
				characters.forName(name)?: throw ArgumentParseException("A character called '$name' could not be found")
			}
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
				.map { quoteCommandArgument(it) }
				.toList()
	}
}