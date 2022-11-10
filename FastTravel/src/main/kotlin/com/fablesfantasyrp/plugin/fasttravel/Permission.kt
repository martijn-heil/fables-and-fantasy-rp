package com.fablesfantasyrp.plugin.fasttravel

object Permission {
	const val prefix = "fables.fasttravel"

	object Command {
		private const val prefix = Permission.prefix + ".command"

		object FastTravel {
			private const val prefix = Permission.Command.prefix + ".fasttravel"
			const val List = "${prefix}.list"
			const val Link = "${prefix}.link"
			const val Unlink = "${prefix}.unlink"
		}
	}
}
