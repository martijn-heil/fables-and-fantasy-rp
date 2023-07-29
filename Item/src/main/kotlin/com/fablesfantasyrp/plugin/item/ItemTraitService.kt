package com.fablesfantasyrp.plugin.item

import org.bukkit.inventory.ItemStack

interface ItemTraitService {
	fun getTraits(item: ItemStack): Set<String>
}
