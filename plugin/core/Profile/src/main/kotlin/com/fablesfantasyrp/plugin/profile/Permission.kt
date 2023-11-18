package com.fablesfantasyrp.plugin.profile

object Permission {
	const val prefix = "fables.profile"

	object Command {
		private const val prefix = Permission.prefix + ".command"

		object CommandProfile {
			private const val prefix = Permission.Command.prefix + ".profile"
			const val List = "${prefix}.list"
			const val New = "${prefix}.new"
			const val Become = "${prefix}.become"
			const val Transfer = "${prefix}.transfer"
			const val Current = "${prefix}.current"
			const val SetActive = "${prefix}.setactive"
		}
	}
}
