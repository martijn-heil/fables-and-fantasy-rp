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
