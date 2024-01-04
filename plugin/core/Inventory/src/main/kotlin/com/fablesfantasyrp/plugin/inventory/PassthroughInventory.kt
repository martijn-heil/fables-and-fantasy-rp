package com.fablesfantasyrp.plugin.inventory

import com.fablesfantasyrp.plugin.database.CacheMarker
import com.fablesfantasyrp.plugin.database.repository.DirtyMarker
import com.fablesfantasyrp.plugin.utils.SerializableItemStack
import org.bukkit.entity.HumanEntity
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack


/**
 * A serializable inventory with read/write-through operation
 */
open class PassthroughInventory
constructor(private val persistentContent: Array<SerializableItemStack?>,
			dirtyMarker: DirtyMarker<in SerializableInventory>? = null,
			@Transient var cacheMarker: CacheMarker<in PassthroughInventory>? = null) : SerializableInventory(persistentContent, dirtyMarker) {

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

			if (oldInventory == null && newInventory != null) {
				cacheMarker?.markStrong(this)
			} else if (oldInventory != null && newInventory == null) {
				dirtyMarker?.markDirty(this)
				cacheMarker?.markWeak(this)
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
				super.contents = value
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

	override fun clone() = PassthroughInventory(persistentContent.clone())

	companion object {
		@JvmStatic
		private val serialVersionUID = 1L

		fun copyOfBukkitInventory(inventory: Inventory) =
			PassthroughInventory(inventory.contents!!.map { it?.let { SerializableItemStack(it) }}.toTypedArray())
	}
}
