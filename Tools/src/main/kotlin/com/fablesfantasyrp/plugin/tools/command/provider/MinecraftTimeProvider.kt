package com.fablesfantasyrp.plugin.tools.command.provider

import com.sk89q.intake.argument.ArgumentException
import com.sk89q.intake.argument.ArgumentParseException
import com.sk89q.intake.argument.CommandArgs
import com.sk89q.intake.argument.Namespace
import com.sk89q.intake.parametric.Provider
import com.sk89q.intake.parametric.ProvisionException

class MinecraftTimeProvider : Provider<MinecraftTime> {
	override fun isProvided() = false

	private val constants: Map<String, Long> = mapOf(
		"day" to 1000,
		"noon" to 6000,
		"sunset" to 12000,
		"night" to 13000,
		"midnight" to 18000,
		"sunrise" to 23000,
	)

	@Throws(ArgumentException::class, ProvisionException::class)
	override fun get(arguments: CommandArgs, modifiers: List<Annotation>): MinecraftTime {
		val value = arguments.peek()
		val time = constants[value] ?: value.toLongOrNull() ?: throw ArgumentParseException("Invalid time '$value'")
		return MinecraftTime(time)
	}

	override fun getSuggestions(prefix: String, locals: Namespace, modifiers: List<Annotation>): List<String>
			= constants.keys.toList()
}
