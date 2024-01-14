package com.fablesfantasyrp.plugin.activity

object Permission {
	const val prefix = "fables.activity"

	object Command {
		const val prefix = Permission.prefix + ".command"
		const val Activity = "${prefix}.activity"
	}
}
