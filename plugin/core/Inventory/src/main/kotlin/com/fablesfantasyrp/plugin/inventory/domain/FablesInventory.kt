/*
 * Fables and Fantasy RP kotlin plugins.
 * Copyright (C) 2024  Martijn Heil
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.fablesfantasyrp.plugin.inventory.domain

import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.HumanEntity
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryHolder
import org.bukkit.inventory.ItemStack

interface FablesInventory : List<ItemStack?> {
	var contents: List<ItemStack?>
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
