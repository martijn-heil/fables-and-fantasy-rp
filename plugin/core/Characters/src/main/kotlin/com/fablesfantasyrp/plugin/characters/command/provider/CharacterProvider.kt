package com.fablesfantasyrp.plugin.characters.command.provider

import com.fablesfantasyrp.plugin.characters.domain.entity.Character
import com.fablesfantasyrp.plugin.characters.domain.repository.CharacterRepository
import com.fablesfantasyrp.plugin.profile.ProfileManager
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

class CharacterProvider(private val server: Server,
						private val characters: CharacterRepository,
						private val profileManager: ProfileManager) : Provider<Character> {
	override val isProvided: Boolean = false

	override suspend fun get(arguments: CommandArgs, modifiers: List<Annotation>): Character {
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
				characters.forId(id) ?: throw ArgumentParseException("A character with id '$id' could not be found")
			} else {
				characters.forName(name)?: throw ArgumentParseException("A character called '$name' could not be found")
			}
		} else if (targetAnnotation != null) {
			val player = sender as? Player
			val currentProfile = player?.let { profileManager.getCurrentForPlayer(it) }
			val currentCharacter = currentProfile?.let { characters.forProfile(it) }
			if (currentCharacter == null) {
				arguments.next() // Generate MissingArgumentException
				throw IllegalStateException()
			}
			return currentCharacter
		} else {
			// Generate MissingArgumentException
			arguments.next()
			throw IllegalStateException()
		}
	}

	override suspend fun getSuggestions(prefix: String, locals: Namespace, modifiers: List<Annotation>): List<String> {
			return characters.allNames().asSequence()
				.filter { it.startsWith(prefix.removePrefix("\""), true) }
				.map { quoteCommandArgument(it) }
				.toList()
	}
}
