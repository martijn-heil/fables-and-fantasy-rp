package com.fablesfantasyrp.plugin.playeresp

object Permission {
	const val prefix = "fables.playeresp"

	object Command {
		private const val prefix = Permission.prefix + ".command"
		const val Playeresp = "${prefix}.playeresp"
	}
}
