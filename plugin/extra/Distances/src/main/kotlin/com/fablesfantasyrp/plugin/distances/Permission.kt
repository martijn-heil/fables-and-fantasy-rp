package com.fablesfantasyrp.plugin.distances

internal object Permission {
	const val prefix = "fables.distances"

	object Command {
		private const val prefix = Permission.prefix + ".command"
		const val Near = "${prefix}.near"
		const val Distance = "${prefix}.distance"
	}
}
