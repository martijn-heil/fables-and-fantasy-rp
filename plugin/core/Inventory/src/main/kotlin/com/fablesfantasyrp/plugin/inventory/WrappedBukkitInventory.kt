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
package com.fablesfantasyrp.plugin.inventory

import com.fablesfantasyrp.plugin.inventory.domain.FablesInventory
import org.bukkit.entity.HumanEntity
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack


class WrappedBukkitInventory(private val bukkitInventory: Inventory) : FablesInventory {
	override val size: Int
		get() = bukkitInventory.size

	override var contents: List<ItemStack?>
		get() = bukkitInventory.contents.toList()
		set(value) { bukkitInventory.contents = value.toTypedArray() }

	override val viewers: List<HumanEntity>
		get() = bukkitInventory.viewers

	override fun clear() {
		bukkitInventory.clear()
	}

	override operator fun set(index: Int, value: ItemStack?) {
		if (index < 0 || index >= size) throw IndexOutOfBoundsException()
		bukkitInventory.setItem(index, value)
	}

	override fun get(index: Int): ItemStack? = bukkitInventory.getItem(index)
	override fun isEmpty(): Boolean = bukkitInventory.contents?.find { it != null } == null
	override fun iterator(): Iterator<ItemStack?> = (bukkitInventory.contents ?: emptyArray()).iterator()
	override fun listIterator(): ListIterator<ItemStack?> = listIterator(0)
	override fun listIterator(index: Int): ListIterator<ItemStack?> {
		return object : ListIterator<ItemStack?> {
			private var currentIndex = index
			override fun hasNext() = currentIndex < size-1
			override fun hasPrevious() = currentIndex > 0
			override fun next() = get(++currentIndex)
			override fun nextIndex() = currentIndex + 1
			override fun previous() = get(--currentIndex)
			override fun previousIndex() = currentIndex - 1
		}
	}

	override fun subList(fromIndex: Int, toIndex: Int): List<ItemStack?> = bukkitInventory.contents?.toList()?.subList(fromIndex, toIndex) ?: emptyList()
	override fun lastIndexOf(element: ItemStack?): Int = bukkitInventory.contents?.lastIndexOf(element) ?: -1
	override fun indexOf(element: ItemStack?): Int = bukkitInventory.contents?.indexOf(element) ?: -1
	override fun containsAll(elements: Collection<ItemStack?>): Boolean = bukkitInventory.contents?.toList()?.containsAll(elements) ?: false
	override fun contains(element: ItemStack?): Boolean = bukkitInventory.contents?.contains(element) ?: false
}
