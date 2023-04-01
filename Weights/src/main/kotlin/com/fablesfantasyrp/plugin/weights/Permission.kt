package com.fablesfantasyrp.plugin.weights

object Permission {
	const val prefix = "fables.weights"

	object Command {
		private const val prefix = Permission.prefix + ".command"
		const val Weights = "$prefix.weights"
	}
}
