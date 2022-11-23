package com.fablesfantasyrp.plugin.playerinstance.command.provider

import com.fablesfantasyrp.plugin.playerinstance.currentPlayerInstance
import com.fablesfantasyrp.plugin.playerinstance.data.entity.EntityPlayerInstanceRepository
import com.fablesfantasyrp.plugin.playerinstance.data.entity.PlayerInstance
import com.gitlab.martijn_heil.nincommands.common.CommandTarget
import com.sk89q.intake.argument.ArgumentParseException
import com.sk89q.intake.argument.CommandArgs
import com.sk89q.intake.argument.Namespace
import com.sk89q.intake.parametric.Provider
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class PlayerInstanceProvider(private val playerInstances: EntityPlayerInstanceRepository<*>) : Provider<PlayerInstance> {
	override fun isProvided(): Boolean = false

	override fun get(arguments: CommandArgs, modifiers: List<Annotation>): PlayerInstance? {
		val sender = arguments.namespace.get("sender") as CommandSender
		val targetAnnotation = modifiers.find { it is CommandTarget }

		if (arguments.hasNext()) {
			val id = arguments.nextInt()
			return playerInstances.forId(id) ?: throw ArgumentParseException("A palyer instance with id '$id' could not be found")
		} else if (targetAnnotation != null && sender is Player && sender.currentPlayerInstance != null) {
			return sender.currentPlayerInstance!!
		} else {
			// Generate MissingArgumentException
			arguments.next()
		}
		return null
	}

	override fun getSuggestions(prefix: String, locals: Namespace, modifiers: List<Annotation>): List<String> {
		return playerInstances.allIds().map { it.toString() }.filter { it.startsWith(prefix) }
	}
}
