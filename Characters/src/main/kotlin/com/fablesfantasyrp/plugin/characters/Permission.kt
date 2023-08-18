package com.fablesfantasyrp.plugin.characters

object Permission {
	const val prefix = "fables.characters"
	const val Staff = "${prefix}.staff"
	const val Any = "${prefix}.any"

	object Change {
		private const val prefix = Permission.Command.Characters.prefix + ".change"
		const val Description = "${prefix}.description"
		const val Age = "${prefix}.age"
		const val Name = "${prefix}.name"
		const val Stats = "${prefix}.stats"
		const val Race = "${prefix}.race"
		const val Gender = "${prefix}.gender"
	}

	object Command {
		const val prefix = Permission.prefix + ".command"

		object Characters {
			const val prefix = Permission.Command.prefix + ".characters"
			const val New = "${prefix}.new"
			const val List = "${prefix}.list"
			const val Listunowned = "${prefix}.listunowned"
			const val Card = "${prefix}.card"
			const val Kill = "${prefix}.kill"
			const val Resurrect = "${prefix}.resurrect"
			const val Shelf = "${prefix}.shelf"
			const val Unshelf = "${prefix}.unshelf"
			const val SetRace = "${prefix}.setrace"
			const val SetAge = "${prefix}.setage"
			const val Become = "${prefix}.become"

			object Stats {
				private const val prefix = Permission.Command.Characters.prefix + ".stats"
				const val Set = "${prefix}.set"
				const val Edit = "${prefix}.edit"
			}
		}
	}
}
