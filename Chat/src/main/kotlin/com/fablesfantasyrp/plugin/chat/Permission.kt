package com.fablesfantasyrp.plugin.chat

object Permission {
	const val prefix = "fables.chat"
	const val Format = "${prefix}.format"

	object Channel {
		const val prefix = Permission.prefix + ".channel"
		const val Ic = "${prefix}.ic"
		const val Ooc = "${prefix}.ooc"
		const val Looc = "${prefix}.looc"
		const val Spectator = "${prefix}.spectator"
		const val Staff = "${prefix}.staff"
	}

	object Command {
		private const val prefix = Permission.prefix + ".command"
		const val Togglechat = "${prefix}.togglechat"
		const val Chatcolor = "${prefix}.chatcolor"
		const val Chatchannel = "${prefix}.chatchannel"
		const val Togglereceptionindicator = "${prefix}.togglereceptionindicator"
		const val ChatSpy = "${prefix}.chatspy"
	}

	object Exempt {
		private const val prefix = Permission.prefix + ".exempt"
		const val ChatSpy = "${prefix}.chatspy"
	}
}
