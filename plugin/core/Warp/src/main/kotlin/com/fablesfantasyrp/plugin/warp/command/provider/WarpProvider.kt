package com.fablesfantasyrp.plugin.warp.command.provider

import com.fablesfantasyrp.plugin.warp.data.SimpleWarp
import com.fablesfantasyrp.plugin.warp.data.SimpleWarpRepository
import com.fablesfantasyrp.caturix.argument.ArgumentException
import com.fablesfantasyrp.caturix.argument.ArgumentParseException
import com.fablesfantasyrp.caturix.argument.CommandArgs
import com.fablesfantasyrp.caturix.argument.Namespace
import com.fablesfantasyrp.caturix.parametric.Provider
import com.fablesfantasyrp.caturix.parametric.ProvisionException


class WarpProvider(private val warps: SimpleWarpRepository) : Provider<SimpleWarp> {
	override val isProvided = false

	@Throws(ArgumentException::class, ProvisionException::class)
	override fun get(arguments: CommandArgs, modifiers: List<Annotation>): SimpleWarp {
		val id = arguments.next()
		return warps.forId(id) ?: throw ArgumentParseException("A warp with name '$id' does not exist!")
	}

	override fun getSuggestions(prefix: String, locals: Namespace, modifiers: List<Annotation>): List<String>
			= warps.allIds().filter { it.startsWith(prefix) }
}
