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
			const val Kill = "${Command.prefix}.kill"
			const val Resurrect = "${Command.prefix}.resurrect"
			const val Shelf = "${Command.prefix}.shelf"
			const val Unshelf = "${Command.prefix}.unshelf"
			const val SetRace = "${Command.prefix}.setrace"

			object Stats {
				private const val prefix = Permission.Command.Characters.prefix + ".characters"
				const val Set = "${Command.prefix}.set"
				const val Edit = "${Command.prefix}.edit"
			}
		}
	}
}
