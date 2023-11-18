package com.fablesfantasyrp.plugin.text

object Permission {
	const val prefix = "fables.chat"
	const val Format = "$prefix.format"

	object Channel {
		private const val prefix = Permission.prefix + ".channel"
		const val Ic = "$prefix.ic"
		const val Ooc = "$prefix.ooc"
		const val Looc = "$prefix.looc"
		const val Spectator = "$prefix.spectator"
		const val Staff = "$prefix.staff"
	}

	object Command {
		private const val prefix = Permission.prefix + ".command"
		const val Togglechat = "$prefix.togglechat"
		const val Chatcolor = "$prefix.chatcolor"
	}
}
