package com.fablesfantasyrp.plugin.inventory

import com.fablesfantasyrp.plugin.utils.SerializableItemStack
import org.bukkit.entity.HumanEntity
import org.bukkit.inventory.ItemStack
import java.io.Serializable

open class SerializableInventory constructor(private val persistentContent: Array<SerializableItemStack?>,
											 @Transient override val size: Int) : FablesInventory, Serializable {

	override val viewers: List<HumanEntity> get() = emptyList()

	open var contents: List<ItemStack?>
		get() = persistentContent.map { it?.itemStack }
		set(value) {
			value.map { if (it != null) SerializableItemStack(it) else null }
					.toTypedArray().copyInto(persistentContent)
		}

	override fun clear() {
		for (i in persistentContent.indices) {
			persistentContent[i] = null
		}
	}

	override operator fun set(index: Int, value: ItemStack?) {
		if (index < 0 || index >= size) throw IndexOutOfBoundsException()
		persistentContent[index] = value?.let { SerializableItemStack(it) }
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

	companion object {
		@JvmStatic
		private val serialVersionUID = 1L
	}
}
