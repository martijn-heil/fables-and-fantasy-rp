package com.fablesfantasyrp.plugin.lodestones

object Permission {
	const val prefix = "fables.lodestones"
	const val Slots = "${prefix}.slots"

	object Command {
		private const val prefix = Permission.prefix + ".command"

		object Lodestone {
			private const val prefix = Command.prefix + ".lodestone"
			const val Create = "$prefix.create"
			const val Move = "$prefix.move"
			const val Rename = "$prefix.rename"
			const val List = "$prefix.list"
			const val Destroy = "$prefix.destroy"
		}

		object Lodebanner {
			private const val prefix = Command.prefix + ".lodebanner"
			const val Create = "$prefix.create"
			const val List = "$prefix.list"
			const val Destroy = "$prefix.destroy"
		}
	}
}
