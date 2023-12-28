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
	override fun get(arguments: CommandArgs, modifiers: List<Annotation>): MinecraftTime {
		val value = arguments.next()
		val time = constants[value] ?: value.toLongOrNull() ?: throw ArgumentParseException("Invalid time '$value'")
		return MinecraftTime(time)
	}

	override fun getSuggestions(prefix: String, locals: Namespace, modifiers: List<Annotation>): List<String>
			= constants.keys.toList()
}
