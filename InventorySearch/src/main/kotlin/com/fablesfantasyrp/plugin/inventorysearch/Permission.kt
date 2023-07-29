package com.fablesfantasyrp.plugin.inventorysearch

object Permission {
	const val prefix = "fables.inventorysearch"

	object Command {
		private const val prefix = Permission.prefix + ".command"
		const val InventorySearch = "$prefix.inventorysearch"
	}
}
