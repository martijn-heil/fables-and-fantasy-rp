package com.fablesfantasyrp.plugin.inventory

import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.HumanEntity
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryHolder
import org.bukkit.inventory.ItemStack

interface FablesInventory : List<ItemStack?> {
	val viewers: List<HumanEntity>
	fun clear()
	operator fun set(index: Int, value: ItemStack?)

	fun removeAll(item: ItemStack) {
		for (i in this.indices) {
			if (this[i] == item) this[i] = null
		}
	}

	fun removeAll(material: Material) {
		for (i in this.indices) {
			if (this[i]?.type == material) this[i] = null
		}
	}

	fun asGenericInventoryCopy(owner: InventoryHolder, title: Component): Inventory {
		// Generic inventories have to be of a size that is a multiple of 9
		var calculatedSize = size
		if (calculatedSize % 9 != 0) calculatedSize = calculatedSize / 9 * 9 + 9

		val inventory = Bukkit.createInventory(owner, calculatedSize, title)
		this.forEachIndexed { index, item -> inventory.setItem(index, item) }
		return inventory
	}
}
