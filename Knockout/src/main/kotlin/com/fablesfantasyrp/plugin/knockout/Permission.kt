package com.fablesfantasyrp.plugin.knockout

object Permission {
	const val prefix = "fables.knockout"

	object Command {
		private const val prefix = Permission.prefix + ".command"
		const val Knockout = "$prefix.knockout"
		const val Revive = "$prefix.revive"
	}
}
