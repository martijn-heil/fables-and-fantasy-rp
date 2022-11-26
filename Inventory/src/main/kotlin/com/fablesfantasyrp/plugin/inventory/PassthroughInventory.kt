package com.fablesfantasyrp.plugin.inventory

import com.fablesfantasyrp.plugin.utils.SerializableItemStack
import org.bukkit.entity.HumanEntity
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack


/**
 * A serializable inventory with read/write-through operation
 */
open class PassthroughInventory
constructor(private val persistentContent: Array<SerializableItemStack?>, size: Int) : SerializableInventory(persistentContent, size) {

	@Transient
	open var bukkitInventory: Inventory? = null
		set(value) {
			if (value == field) return
			val newInventory = value
			val oldInventory = field
			field = null

			if (oldInventory != null) {
				oldInventory.contents!!.map { it?.let { SerializableItemStack(it) } }.toTypedArray().copyInto(persistentContent)
			}

			if (newInventory != null) {
				newInventory.contents = contents.toTypedArray()
			}

			field = newInventory
		}

	@Transient
	private val indirectViewers = ArrayList<HumanEntity>()

	override val viewers: List<HumanEntity> get() = indirectViewers.plus(bukkitInventory?.viewers ?: emptyList())

	override var contents: List<ItemStack?>
		get() = bukkitInventory?.contents?.toList() ?: super.contents
		set(value) {
			val inventory = bukkitInventory
			if (inventory != null) {
				inventory.contents = value.toTypedArray()
			} else {
				super.contents = contents
			}
		}

	override fun clear() {
		if (bukkitInventory != null) {
			bukkitInventory!!.clear()
		} else {
			super.clear()
		}
	}

	override operator fun set(index: Int, value: ItemStack?) {
		if (index < 0 || index > 40) throw IndexOutOfBoundsException()

		if (bukkitInventory != null) {
			bukkitInventory!!.setItem(index, value)
		} else {
			super.set(index, value)
		}
	}

	companion object {
		@JvmStatic
		private val serialVersionUID = 1L
	}
}
