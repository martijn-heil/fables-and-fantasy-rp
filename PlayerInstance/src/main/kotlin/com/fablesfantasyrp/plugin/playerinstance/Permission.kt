package com.fablesfantasyrp.plugin.playerinstance

object Permission {
	const val prefix = "fables.playerinstance"

	object Command {
		private const val prefix = Permission.prefix + ".command"

		object FastTravel {
			private const val prefix = Permission.Command.prefix + ".playerinstance"
			const val List = "${prefix}.list"
		}
	}
}
