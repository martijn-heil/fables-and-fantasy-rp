package com.fablesfantasyrp.plugin.characters

object Permission {
	const val prefix = "fables.characters"

	object Command {
		private const val prefix = Permission.prefix + ".command"
		const val Updatestats = "${prefix}.updatestats"
		const val Cardother = "${prefix}.cardother"
		const val Listcharacters = "${prefix}.listchars"
	}
}
