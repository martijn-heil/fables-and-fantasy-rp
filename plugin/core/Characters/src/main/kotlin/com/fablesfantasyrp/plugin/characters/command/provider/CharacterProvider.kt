package com.fablesfantasyrp.plugin.characters.command.provider

import com.fablesfantasyrp.plugin.characters.domain.entity.Character
import com.fablesfantasyrp.plugin.characters.domain.repository.CharacterRepository
import com.fablesfantasyrp.plugin.profile.ProfileManager
import com.fablesfantasyrp.plugin.utils.quoteCommandArgument
import com.gitlab.martijn_heil.nincommands.common.CommandTarget
import com.sk89q.intake.argument.ArgumentParseException
import com.sk89q.intake.argument.CommandArgs
import com.sk89q.intake.argument.Namespace
import com.sk89q.intake.parametric.Provider
import kotlinx.coroutines.runBlocking
import org.bukkit.Server
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.permissions.Permissible

class CharacterProvider(private val server: Server,
						private val characters: CharacterRepository,
						private val profileManager: ProfileManager) : Provider<Character> {
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

			return runBlocking {
				if (name.startsWith("#")) {
					val id = name.removePrefix("#").toIntOrNull() ?: throw ArgumentParseException("Invalid identifier")
					characters.forId(id)
				} else {
					characters.forName(name)
						?: throw ArgumentParseException("A character called '$name' could not be found")
				}
			}
		} else if (targetAnnotation != null) {
			val player = sender as? Player
			val currentProfile = player?.let { profileManager.getCurrentForPlayer(it) }
			val currentCharacter = currentProfile?.let { runBlocking { characters.forProfile(it) } }
			if (currentCharacter == null) {
				arguments.next() // Generate MissingArgumentException
				return null
			}
			return currentCharacter
		} else {
			// Generate MissingArgumentException
			arguments.next()
			return null
		}
	}

	override fun getSuggestions(prefix: String, locals: Namespace, modifiers: List<Annotation>): List<String> {
			return runBlocking {
				characters.allNames().asSequence()
					.filter { it.startsWith(prefix.removePrefix("\""), true) }
					.map { quoteCommandArgument(it) }
					.toList()
			}
	}
}
