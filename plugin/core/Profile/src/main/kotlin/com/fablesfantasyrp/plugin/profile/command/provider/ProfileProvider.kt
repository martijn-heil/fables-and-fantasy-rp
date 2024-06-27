/*
 * Fables and Fantasy RP kotlin plugins.
 * Copyright (C) 2024  Martijn Heil
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.fablesfantasyrp.plugin.profile.command.provider

import com.fablesfantasyrp.plugin.profile.ProfileManager
import com.fablesfantasyrp.plugin.profile.command.annotation.AllowPlayerName
import com.fablesfantasyrp.plugin.profile.data.entity.EntityProfileRepository
import com.fablesfantasyrp.plugin.profile.data.entity.Profile
import com.fablesfantasyrp.caturix.spigot.common.CommandTarget
import com.fablesfantasyrp.caturix.argument.ArgumentParseException
import com.fablesfantasyrp.caturix.argument.CommandArgs
import com.fablesfantasyrp.caturix.argument.Namespace
import com.fablesfantasyrp.caturix.parametric.Provider
import com.fablesfantasyrp.caturix.parametric.ProvisionException
import org.bukkit.Server
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.permissions.Permissible

class ProfileProvider(private val profiles: EntityProfileRepository,
					  private val profileManager: ProfileManager,
					  private val playerProvider: Provider<Player>,
					  private val server: Server) : Provider<Profile> {
	override val isProvided: Boolean = false

	override suspend fun get(arguments: CommandArgs, modifiers: List<Annotation>): Profile {
		val sender = arguments.namespace.get("sender") as CommandSender
		val targetAnnotation = modifiers.find { it is CommandTarget } as? CommandTarget
		val allowPlayerName = modifiers.any { it is AllowPlayerName }

		if (arguments.hasNext()) {
			val peek = arguments.peek()

			val permissible = arguments.namespace.get(Permissible::class.java)!!

			if (targetAnnotation != null && targetAnnotation.value.isNotBlank() && !permissible.hasPermission(targetAnnotation.value)) {
				throw ArgumentParseException("You need " + targetAnnotation.value)
			}

			return if (peek.startsWith('#')) {
				val arg = arguments.next()
				val id = arg.removePrefix("#").toIntOrNull() ?: throw ArgumentParseException("Invalid id '$arg'")
				profiles.forId(id) ?: throw ArgumentParseException("A profile with id '$id' could not be found")
			} else if (allowPlayerName) {
				val player = playerProvider.get(arguments, modifiers)!!
				profileManager.getCurrentForPlayer(player) ?: throw ProvisionException("This player is not currently on a profile")
			} else {
				val arg = arguments.next()
				throw ArgumentParseException("Invalid id '$arg'")
			}
		} else if (targetAnnotation != null && sender is Player && profileManager.getCurrentForPlayer(sender) != null) {
			return profileManager.getCurrentForPlayer(sender)!!
		} else {
			// Generate MissingArgumentException
			arguments.next()
			throw IllegalStateException()
		}
	}

	override suspend fun getSuggestions(prefix: String, locals: Namespace, modifiers: List<Annotation>): List<String> {
		val allowPlayerName = modifiers.any { it is AllowPlayerName }
		return if (allowPlayerName) {
			server.onlinePlayers.map { it.name }.filter { it.lowercase().startsWith(prefix.lowercase()) }
		} else {
			profiles.allIds()
					.map { "#$it" }
					.filter { it.startsWith(prefix) }
		}
	}
}
