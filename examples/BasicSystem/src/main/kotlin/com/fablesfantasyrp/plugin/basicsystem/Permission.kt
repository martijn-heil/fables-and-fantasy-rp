package com.fablesfantasyrp.plugin.basicsystem

object Permission {
	const val prefix = "fables.fasttravel"

	object Command {
		private const val prefix = Permission.prefix + ".command"
		const val Tryepicjump = "$prefix.tryepicjump"

		object Slidingdoor {
			private const val prefix = Command.prefix + ".slidingdoor"
			const val Count = "$prefix.count"
		}
	}
}
