package com.fablesfantasyrp.plugin.profile.command.provider

import com.fablesfantasyrp.plugin.profile.ProfileManager
import com.fablesfantasyrp.plugin.profile.data.entity.EntityProfileRepository
import com.fablesfantasyrp.plugin.profile.data.entity.Profile
import com.gitlab.martijn_heil.nincommands.common.CommandTarget
import com.sk89q.intake.argument.ArgumentParseException
import com.sk89q.intake.argument.CommandArgs
import com.sk89q.intake.argument.Namespace
import com.sk89q.intake.parametric.Provider
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.permissions.Permissible

class ProfileProvider(private val profiles: EntityProfileRepository,
					  private val profileManager: ProfileManager) : Provider<Profile> {
	override fun isProvided(): Boolean = false

	override fun get(arguments: CommandArgs, modifiers: List<Annotation>): Profile? {
		val sender = arguments.namespace.get("sender") as CommandSender
		val targetAnnotation = modifiers.find { it is CommandTarget } as? CommandTarget

		if (arguments.hasNext()) {
			val id = arguments.nextInt()
			val permissible = arguments.namespace.get(Permissible::class.java)!!

			if (targetAnnotation != null && targetAnnotation.value.isNotBlank() && !permissible.hasPermission(targetAnnotation.value)) {
				throw ArgumentParseException("You need " + targetAnnotation.value)
			}

			return profiles.forId(id)
					?: throw ArgumentParseException("A profile with id '$id' could not be found")
		} else if (targetAnnotation != null && sender is Player && profileManager.getCurrentForPlayer(sender) != null) {
			return profileManager.getCurrentForPlayer(sender)!!
		} else {
			// Generate MissingArgumentException
			arguments.next()
		}
		return null
	}

	override fun getSuggestions(prefix: String, locals: Namespace, modifiers: List<Annotation>): List<String> {
		return profiles.allIds()
				.map { it.toString() }
				.filter { it.startsWith(prefix) }
	}
}
