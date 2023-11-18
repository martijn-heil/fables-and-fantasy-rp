package com.fablesfantasyrp.plugin.bell

object Permission {
	const val prefix = "fables.bell"

	object Command {
		private const val prefix = Permission.prefix + ".command"

		object Bell {
			private const val prefix = Command.prefix + ".bell"
			const val Create = "$prefix.create"
			const val Ring = "$prefix.ring"
			const val List = "$prefix.list"
			const val Destroy = "$prefix.destroy"
		}
	}
}
