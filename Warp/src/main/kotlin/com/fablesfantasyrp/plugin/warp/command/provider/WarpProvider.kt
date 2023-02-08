package com.fablesfantasyrp.plugin.warp.command.provider

import com.fablesfantasyrp.plugin.warp.data.SimpleWarp
import com.fablesfantasyrp.plugin.warp.data.SimpleWarpRepository
import com.sk89q.intake.argument.ArgumentException
import com.sk89q.intake.argument.ArgumentParseException
import com.sk89q.intake.argument.CommandArgs
import com.sk89q.intake.argument.Namespace
import com.sk89q.intake.parametric.Provider
import com.sk89q.intake.parametric.ProvisionException


class WarpProvider(private val warps: SimpleWarpRepository) : Provider<SimpleWarp> {
	override fun isProvided() = false

	@Throws(ArgumentException::class, ProvisionException::class)
	override fun get(arguments: CommandArgs, modifiers: List<Annotation>): SimpleWarp {
		val id = arguments.next()
		return warps.forId(id) ?: throw ArgumentParseException("A warp with name '$id' does not exist!")
	}

	override fun getSuggestions(prefix: String, locals: Namespace, modifiers: List<Annotation>): List<String>
			= warps.allIds().toList()
}
