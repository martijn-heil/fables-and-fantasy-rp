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

import com.fablesfantasyrp.plugin.database.repository.DirtyMarker
import com.fablesfantasyrp.plugin.inventory.domain.FablesInventory
import com.fablesfantasyrp.plugin.utils.SerializableItemStack
import org.bukkit.entity.HumanEntity
import org.bukkit.inventory.ItemStack
import java.io.Serializable

open class SerializableInventory
	constructor(private val persistentContent: Array<SerializableItemStack?>,
				@Transient var dirtyMarker: DirtyMarker<in SerializableInventory>? = null)
		: FablesInventory, Serializable, Cloneable {

	override val size get() = persistentContent.size

	override val viewers: List<HumanEntity> get() = emptyList()

	override var contents: List<ItemStack?>
		get() = persistentContent.map { it?.itemStack }
		set(value) {
			value.map { if (it != null) SerializableItemStack(it) else null }
					.toTypedArray().copyInto(persistentContent)
			dirtyMarker?.markDirty(this)
		}

	override fun clear() {
		for (i in persistentContent.indices) {
			persistentContent[i] = null
		}
		dirtyMarker?.markDirty(this)
	}

	override operator fun set(index: Int, value: ItemStack?) {
		if (index < 0 || index >= size) throw IndexOutOfBoundsException()
		persistentContent[index] = value?.let { SerializableItemStack(it) }
		dirtyMarker?.markDirty(this)
	}

	override fun get(index: Int): ItemStack? = contents[index]
	override fun isEmpty(): Boolean = contents.find { it != null } == null
	override fun iterator(): Iterator<ItemStack?> = contents.iterator()
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

	override fun subList(fromIndex: Int, toIndex: Int): List<ItemStack?> = contents.toList().subList(fromIndex, toIndex)
	override fun lastIndexOf(element: ItemStack?): Int = contents.lastIndexOf(element)
	override fun indexOf(element: ItemStack?): Int = contents.indexOf(element)
	override fun containsAll(elements: Collection<ItemStack?>): Boolean = contents.toList().containsAll(elements)
	override fun contains(element: ItemStack?): Boolean = contents.contains(element)
	public override fun clone() = SerializableInventory(persistentContent.clone())

	companion object {
		@JvmStatic
		private val serialVersionUID = 1L
	}
}
