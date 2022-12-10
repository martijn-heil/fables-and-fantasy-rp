package com.fablesfantasyrp.plugin.characters.command.provider

import com.fablesfantasyrp.plugin.characters.data.entity.EntityCharacterRepository
import com.fablesfantasyrp.plugin.playerinstance.PlayerInstanceManager
import com.fablesfantasyrp.plugin.playerinstance.data.entity.PlayerInstance
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

class AllowCharacterNamePlayerInstanceProvider(private val server: Server,
											   private val characterRepository: EntityCharacterRepository,
												private val playerInstanceProvider: Provider<PlayerInstance>,
												private val playerInstanceManager: PlayerInstanceManager) : Provider<PlayerInstance> {

	override fun isProvided(): Boolean = false

	private fun getByPlayerCharacter(arguments: CommandArgs, modifiers: List<Annotation>): PlayerInstance? {
		return characterRepository.forName(arguments.peek())?.playerInstance
	}

	private fun getByPlayerInstanceProvider(arguments: CommandArgs, modifiers: List<Annotation>): PlayerInstance? {
		return playerInstanceProvider.get(arguments, modifiers)
	}

	override fun get(arguments: CommandArgs, modifiers: List<Annotation>): PlayerInstance? {
		val sender = arguments.namespace.get("sender") as CommandSender
		val permissible = arguments.namespace.get(Permissible::class.java)!!
		val targetAnnotation = modifiers.find { it is CommandTarget } as? CommandTarget

		if (arguments.hasNext()) {
			val firstAttempt = getByPlayerCharacter(arguments, modifiers)
			val result = if (firstAttempt != null) {
				arguments.next()
				firstAttempt
			} else {
				val secondAttempt = getByPlayerInstanceProvider(arguments, modifiers)
				secondAttempt ?: throw ArgumentParseException("Player instance not found")
			}

			if (targetAnnotation != null &&
					targetAnnotation.value.isNotBlank() &&
					result.owner != sender &&
					!permissible.hasPermission(targetAnnotation.value)) {
				throw ArgumentParseException("You need " + targetAnnotation.value)
			}

			return result
		} else if (targetAnnotation != null && sender is Player) {
			val ownPlayerInstance = playerInstanceManager.getCurrentForPlayer(sender)
			if (ownPlayerInstance == null) arguments.next() // Generate MissingArgumentException
			return ownPlayerInstance
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
