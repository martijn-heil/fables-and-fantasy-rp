package com.fablesfantasyrp.plugin.warp

object Permission {
	const val prefix = "fables.warp"

	object Command {
		private const val prefix = Permission.prefix + ".command"
		const val Warp = "$prefix.warp"
		const val SetWarp = "$prefix.setwarp"
		const val DelWarp = "$prefix.delwarp"
	}
}
