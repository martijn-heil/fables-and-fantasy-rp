package com.fablesfantasyrp.plugin.utils

import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.ListTag
import net.minecraft.nbt.NbtIo
import org.bukkit.craftbukkit.v1_19_R1.inventory.CraftItemStack
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack
import java.io.*
import net.minecraft.world.item.ItemStack as NMSItemStack


fun ItemStack.toBytes(): ByteArray {
	val outputStream = ByteArrayOutputStream()

	val nbtTagCompoundItem = CraftItemStack.asNMSCopy(this).save(CompoundTag())

	val nbtTagListItems = {
		val list = ListTag()
		list.add(nbtTagCompoundItem)
		list
	}

	NbtIo.writeCompressed(nbtTagCompoundItem, outputStream)
	return outputStream.toByteArray()
}

object ItemStackCompanion
fun ItemStackCompanion.fromBytes(data: ByteArray): ItemStack {
	val nbtTagCompoundRoot = NbtIo.readCompressed(data.inputStream())
	val nmsItem = NMSItemStack.of(nbtTagCompoundRoot)
	return CraftItemStack.asBukkitCopy(nmsItem)
}

val PLAYER_HOTBAR_SLOTS = 36..44

class SerializableItemStack() : Externalizable {
	lateinit var itemStack: ItemStack
		private set

	constructor(itemStack: ItemStack) : this() {
		this.itemStack = itemStack
	}

	@Throws(IOException::class)
	override fun writeExternal(objectOutput: ObjectOutput) {
		objectOutput.writeObject(itemStack.toBytes())
	}

	@Throws(IOException::class)
	override fun readExternal(objectInput: ObjectInput) {
		val data = objectInput.readObject() as ByteArray
		itemStack = ItemStackCompanion.fromBytes(data)
	}
}

fun EquipmentSlot.getInventorySlotIndex(heldItemSlot: Int): Int {
	return when (this) {
		EquipmentSlot.HAND -> heldItemSlot
		EquipmentSlot.OFF_HAND -> 40
		EquipmentSlot.FEET -> 36
		EquipmentSlot.LEGS -> 37
		EquipmentSlot.CHEST -> 38
		EquipmentSlot.HEAD -> 39
	}
}
