package com.fablesfantasyrp.plugin.tools.command.provider

import com.sk89q.intake.argument.ArgumentException
import com.sk89q.intake.argument.ArgumentParseException
import com.sk89q.intake.argument.CommandArgs
import com.sk89q.intake.argument.Namespace
import com.sk89q.intake.parametric.Provider
import com.sk89q.intake.parametric.ProvisionException
import org.bukkit.Location
import org.bukkit.Server


class LocationProvider(private val server: Server) : Provider<Location> {
	override fun isProvided() = false

	@Throws(ArgumentException::class, ProvisionException::class)
	override fun get(arguments: CommandArgs, modifiers: List<Annotation>): Location {
		val x = arguments.nextInt()
		val y = arguments.nextInt()
		val z = arguments.next()
		val worldName = arguments.next()
		val world = server.getWorld(worldName) ?: throw ArgumentParseException("Invalid world '$worldName'")

		return Location(world, x.toDouble(), y.toDouble(), z.toDouble())
	}

	override fun getSuggestions(prefix: String, locals: Namespace, modifiers: List<Annotation>): List<String>
			= emptyList()
}
