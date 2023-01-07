package com.fablesfantasyrp.plugin.tools

object Permission {
	const val prefix = "fables.tools"

	object Command {
		private const val prefix = Permission.prefix + ".command"
		const val Invsee = "$prefix.invsee"
		const val Endersee = "$prefix.endersee"
		const val Teleport = "$prefix.teleport"
	}
}
