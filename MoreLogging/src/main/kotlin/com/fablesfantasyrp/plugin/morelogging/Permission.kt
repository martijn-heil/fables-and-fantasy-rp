package com.fablesfantasyrp.plugin.morelogging

object Permission {
	const val prefix = "fables.morelogging"

	object Log {
		private const val prefix = Permission.prefix + ".log"
		const val Receive = "${prefix}.receive"
	}

	object Notices {
		const val prefix = Permission.prefix + ".notices"
		const val Receive = "${prefix}.receive"
		const val Level = "${prefix}.level"
		const val CanSee = "${prefix}.cansee"
	}
}
