package com.fablesfantasyrp.plugin.playerinstance

object Permission {
	const val prefix = "fables.playerinstance"

	object Command {
		private const val prefix = Permission.prefix + ".command"

		object CommandPlayerInstance {
			private const val prefix = Permission.Command.prefix + ".playerinstance"
			const val List = "${prefix}.list"
			const val New = "${prefix}.new"
			const val Become = "${prefix}.become"
			const val Transfer = "${prefix}.become"
		}
	}
}
