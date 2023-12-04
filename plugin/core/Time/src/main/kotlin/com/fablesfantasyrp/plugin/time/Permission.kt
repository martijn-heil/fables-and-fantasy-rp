package com.fablesfantasyrp.plugin.time

object Permission {
	const val prefix = "fables.date"

	object Command {
		const val prefix = Permission.prefix + ".command"
		const val Date = "${prefix}.date"

		object DateTime {
			const val prefix = Permission.Command.prefix + ".datetime"
			const val Set = "${prefix}.set"
			const val DebugInfo = "${prefix}.debuginfo"
		}
	}
}
