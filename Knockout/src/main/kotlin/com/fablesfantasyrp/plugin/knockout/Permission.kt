package com.fablesfantasyrp.plugin.knockout

object Permission {
	const val prefix = "fables.chat"
	const val Format = "$prefix.format"

	object Channel {
		const val prefix = Permission.prefix + ".channel"
		const val Ic = "$prefix.ic"
		const val Ooc = "$prefix.ooc"
		const val Looc = "$prefix.looc"
		const val Spectator = "$prefix.spectator"
		const val Staff = "$prefix.staff"
	}

	object Command {
		private const val prefix = Permission.prefix + ".command"
		const val Knockout = "$prefix.knockout"
		const val Revive = "$prefix.revive"
	}
}
