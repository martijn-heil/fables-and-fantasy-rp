package com.fablesfantasyrp.plugin.customitems.item.carnyx

import com.fablesfantasyrp.plugin.utils.extensions.bukkit.customModel
import com.fablesfantasyrp.plugin.utils.extensions.bukkit.itemStack
import com.fablesfantasyrp.plugin.utils.extensions.bukkit.meta
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

object Carnyx {
	fun matches(item: ItemStack): Boolean {
		return item.type == Material.FERMENTED_SPIDER_EYE && item.itemMeta.customModel == 35
	}

	fun create(): ItemStack {
		return itemStack(Material.FERMENTED_SPIDER_EYE) {
			meta {
				customModel = 35
			}
		}
	}
}
