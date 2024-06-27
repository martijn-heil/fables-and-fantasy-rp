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
package com.fablesfantasyrp.plugin.tools.command.provider

import com.fablesfantasyrp.caturix.argument.ArgumentException
import com.fablesfantasyrp.caturix.argument.ArgumentParseException
import com.fablesfantasyrp.caturix.argument.CommandArgs
import com.fablesfantasyrp.caturix.argument.Namespace
import com.fablesfantasyrp.caturix.parametric.Provider
import com.fablesfantasyrp.caturix.parametric.ProvisionException
import org.bukkit.Location
import org.bukkit.Server


class LocationProvider(private val server: Server) : Provider<Location> {
	override val isProvided = false

	@Throws(ArgumentException::class, ProvisionException::class)
	override suspend fun get(arguments: CommandArgs, modifiers: List<Annotation>): Location {
		val x = arguments.nextInt()
		val y = arguments.nextInt()
		val z = arguments.next()
		val worldName = arguments.next()
		val world = server.getWorld(worldName) ?: throw ArgumentParseException("Invalid world '$worldName'")

		return Location(world, x.toDouble(), y.toDouble(), z.toDouble())
	}

	override suspend fun getSuggestions(prefix: String, locals: Namespace, modifiers: List<Annotation>): List<String>
			= emptyList()
}
