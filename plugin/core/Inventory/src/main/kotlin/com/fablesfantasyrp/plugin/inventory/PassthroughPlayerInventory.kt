package com.fablesfantasyrp.plugin.inventory

import com.fablesfantasyrp.plugin.inventory.domain.FablesPlayerInventory
import com.fablesfantasyrp.plugin.utils.SerializableItemStack
import com.fablesfantasyrp.plugin.utils.extensions.bukkit.getInventorySlotIndex
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.PlayerInventory

/**
 * A serializable player inventory
 *
 *  Indexes 0 through 8 refer to the hotbar. 9 through 35 refer to the main inventory,
 *  counting up from 9 at the top left corner of the inventory, moving to the right,
 *  and moving to the row below it back on the left side when it reaches the end of the row.
 *  It follows the same path in the inventory like you would read a book.
 *
 * Indexes 36 through 39 refer to the armor slots.
 * Though you can set armor with this method using these indexes, you are encouraged to use the provided methods for those slots.
 *
 * Index 40 refers to the off hand (shield) item slot.
 * Though you can set off hand with this method using this index, you are encouraged to use the provided method for this slot.
 */
class PassthroughPlayerInventory constructor(private val persistentContent: Array<SerializableItemStack?>) :
		PassthroughInventory(persistentContent), FablesPlayerInventory {

	override var heldItemSlot: Int = 0
		get() = bukkitPlayerInventory?.heldItemSlot ?: field
		set(value) {
			if(bukkitPlayerInventory != null && bukkitPlayerInventory!!.heldItemSlot != value) bukkitPlayerInventory!!.heldItemSlot = value
			field = value
		}

	@Transient
	override var bukkitInventory: Inventory? = null
		set(value) {
			check(value == null || value is PlayerInventory)
			if (value == field) return
			val newInventory = value as? PlayerInventory
			val oldInventory = field as? PlayerInventory
			field = null

			if (oldInventory != null) {
				oldInventory.contents!!.map { it?.let { SerializableItemStack(it) } }.toTypedArray().copyInto(persistentContent)
				heldItemSlot = oldInventory.heldItemSlot
			}

			if (newInventory != null) {
				newInventory.contents = contents.toTypedArray()
				newInventory.heldItemSlot = heldItemSlot
			}

			if (oldInventory == null && newInventory != null) {
				cacheMarker?.markStrong(this)
			} else if (oldInventory != null && newInventory == null) {
				dirtyMarker?.markDirty(this)
				cacheMarker?.markWeak(this)
			}

			field = newInventory
		}

	val bukkitPlayerInventory: PlayerInventory? get() = if (bukkitInventory != null) bukkitInventory as PlayerInventory else null

	override val armorContents: List<ItemStack?>
		get() = this.slice(36..39)

	override var boots: ItemStack?
		get() = this[36]
		set(value) { this[36] = value }
	override var leggings: ItemStack?
		get() = this[37]
		set(value) { this[37] = value }
	override var chestplate: ItemStack?
		get() = this[38]
		set(value) { this[38] = value }
	override var helmet: ItemStack?
		get() = this[39]
		set(value) { this[39] = value }

	override var itemInMainHand: ItemStack?
		get() = this[heldItemSlot]
		set(value) { this[heldItemSlot] = value }

	override var itemInOffHand: ItemStack?
		get() = this[40]
		set(value) { this[40] = value }

	override operator fun set(equipmentSlot: EquipmentSlot, value: ItemStack?) {
		this[equipmentSlot.getInventorySlotIndex(heldItemSlot)] = value
	}

	override fun clone() = PassthroughPlayerInventory(persistentContent.clone())

	companion object {
		const val size = 41
		fun createEmpty() = PassthroughPlayerInventory(arrayOfNulls(size))

		fun copyOfBukkitInventory(inventory: PlayerInventory) =
				PassthroughPlayerInventory(inventory.contents!!.map { it?.let { SerializableItemStack(it) }}.toTypedArray())

		fun copyOfBukkitInventory(inventory: Inventory) =
				PassthroughPlayerInventory(inventory.contents!!.map { it?.let { SerializableItemStack(it) }}.toTypedArray())

		@JvmStatic
		private val serialVersionUID = 1L
	}
}
