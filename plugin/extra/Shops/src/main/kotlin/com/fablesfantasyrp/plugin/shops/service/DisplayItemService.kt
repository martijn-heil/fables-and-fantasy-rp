package com.fablesfantasyrp.plugin.shops.service

import com.fablesfantasyrp.plugin.utils.extensions.bukkit.BlockIdentifier
import org.bukkit.entity.Item
import org.bukkit.inventory.ItemStack

interface DisplayItemService {
	fun spawnDisplayItem(location: BlockIdentifier, item: ItemStack)
	fun isDisplayItem(item: Item): Boolean
}
