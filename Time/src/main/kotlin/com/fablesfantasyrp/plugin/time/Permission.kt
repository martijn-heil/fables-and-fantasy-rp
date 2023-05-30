package com.fablesfantasyrp.plugin.time

object Permission {
	const val prefix = "fables.date"

	object Command {
		private const val prefix = Permission.prefix + ".command"
		const val Date = "${prefix}.date"

		object DateTime {
			const val Set = "${prefix}.date"
		}
	}
}
