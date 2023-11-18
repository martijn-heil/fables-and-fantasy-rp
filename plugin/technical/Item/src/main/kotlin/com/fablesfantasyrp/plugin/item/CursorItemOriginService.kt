package com.fablesfantasyrp.plugin.item

import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack

interface CursorItemOriginService {
	fun getOriginatingInventory(itemStack: ItemStack): Inventory?
}
