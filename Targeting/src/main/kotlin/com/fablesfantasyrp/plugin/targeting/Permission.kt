package com.fablesfantasyrp.plugin.targeting

object Permission {
	const val prefix = "fables.targeting"

	object Command {
		private const val prefix = Permission.prefix + ".command"

		object Target {
			const val Add = "${prefix}.add"
			const val Remove = "${prefix}.remove"
			const val Select = "${prefix}.select"
			const val List = "${prefix}.list"
		}
	}
}
