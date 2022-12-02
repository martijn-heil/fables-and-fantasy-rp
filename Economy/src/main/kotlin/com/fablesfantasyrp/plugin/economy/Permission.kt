package com.fablesfantasyrp.plugin.economy

object Permission {
	const val prefix = "fables.playerinstance"

	object Command {
		private const val prefix = Permission.prefix + ".command"
		const val Balance = "${Command.prefix}.balance"
		const val Pay = "${Command.prefix}.pay"
	}
}
