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

class MinecraftTimeProvider : Provider<MinecraftTime> {
	override val isProvided = false

	private val constants: Map<String, Long> = mapOf(
		"day" to 1000,
		"noon" to 6000,
		"sunset" to 12000,
		"night" to 13000,
		"midnight" to 18000,
		"sunrise" to 23000,
	)

	@Throws(ArgumentException::class, ProvisionException::class)
	override suspend fun get(arguments: CommandArgs, modifiers: List<Annotation>): MinecraftTime {
		val value = arguments.next()
		val time = constants[value] ?: value.toLongOrNull() ?: throw ArgumentParseException("Invalid time '$value'")
		return MinecraftTime(time)
	}

	override suspend fun getSuggestions(prefix: String, locals: Namespace, modifiers: List<Annotation>): List<String>
			= constants.keys.toList()
}
