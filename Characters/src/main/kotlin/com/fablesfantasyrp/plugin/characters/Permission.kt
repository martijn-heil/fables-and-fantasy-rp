package com.fablesfantasyrp.plugin.characters

object Permission {
	const val prefix = "fables.characters"

	object Command {
		private const val prefix = Permission.prefix + ".command"
		const val Updatestats = "${prefix}.updatestats"

		object Characters {
			private const val prefix = Permission.Command.prefix + ".characters"
			const val New = "${Command.prefix}.new"
			const val List = "${Command.prefix}.list"
			const val Card = "${Command.prefix}.card"
		}
	}
}
