package com.fablesfantasyrp.plugin.targeting

object Permission {
	const val prefix = "fables.com.fablesfantasyrp.plugin.targeting.getTargeting"
	const val Glowingvisuals = "${prefix}.glowingvisuals"

	object Command {
		private const val prefix = Permission.prefix + ".command"

		object Target {
			const val Add = "${prefix}.add"
			const val Remove = "${prefix}.remove"
			const val Select = "${prefix}.select"
			const val List = "${prefix}.list"
			const val Clear = "${prefix}.clear"
			const val Foreach = "${prefix}.foreach"
		}
	}
}
