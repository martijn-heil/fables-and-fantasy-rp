package com.fablesfantasyrp.plugin.wardrobe

object Permission {
	const val prefix = "fables.wardrobe"

	object Command {
		private const val prefix = Permission.prefix + ".command"
		const val Wardrobe = "$prefix.wardrobe"
	}
}
