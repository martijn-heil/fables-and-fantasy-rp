package com.fablesfantasyrp.plugin.lodestones.item

import org.bukkit.Material
import org.bukkit.inventory.ItemStack

object WarpCrystal {
	fun matches(item: ItemStack): Boolean {
		return item.type == Material.NETHER_STAR
	}

	fun create(): ItemStack {
		return ItemStack(Material.NETHER_STAR)
	}
}
