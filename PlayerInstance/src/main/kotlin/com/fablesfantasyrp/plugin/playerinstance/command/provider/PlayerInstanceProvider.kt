package com.fablesfantasyrp.plugin.playerinstance.command.provider

import com.fablesfantasyrp.plugin.playerinstance.PlayerInstanceManager
import com.fablesfantasyrp.plugin.playerinstance.data.entity.EntityPlayerInstanceRepository
import com.fablesfantasyrp.plugin.playerinstance.data.entity.PlayerInstance
import com.gitlab.martijn_heil.nincommands.common.CommandTarget
import com.sk89q.intake.argument.ArgumentParseException
import com.sk89q.intake.argument.CommandArgs
import com.sk89q.intake.argument.Namespace
import com.sk89q.intake.parametric.Provider
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.permissions.Permissible

class PlayerInstanceProvider(private val playerInstances: EntityPlayerInstanceRepository,
							 private val playerInstanceManager: PlayerInstanceManager) : Provider<PlayerInstance> {
	override fun isProvided(): Boolean = false

	override fun get(arguments: CommandArgs, modifiers: List<Annotation>): PlayerInstance? {
		val sender = arguments.namespace.get("sender") as CommandSender
		val targetAnnotation = modifiers.find { it is CommandTarget } as? CommandTarget

		if (arguments.hasNext()) {
			val id = arguments.nextInt()
			val permissible = arguments.namespace.get(Permissible::class.java)!!

			if (targetAnnotation != null && targetAnnotation.value.isNotBlank() && !permissible.hasPermission(targetAnnotation.value)) {
				throw ArgumentParseException("You need " + targetAnnotation.value)
			}

			return playerInstances.forId(id)
					?: throw ArgumentParseException("A player instance with id '$id' could not be found")
		} else if (targetAnnotation != null && sender is Player && playerInstanceManager.getCurrentForPlayer(sender) != null) {
			return playerInstanceManager.getCurrentForPlayer(sender)!!
		} else {
			// Generate MissingArgumentException
			arguments.next()
		}
		return null
	}

	override fun getSuggestions(prefix: String, locals: Namespace, modifiers: List<Annotation>): List<String> {
		return playerInstances.allIds()
				.map { it.toString() }
				.filter { it.startsWith(prefix) }
	}
}
