package com.fablesfantasyrp.plugin.utils

import com.fablesfantasyrp.plugin.utils.extensions.bukkit.toBytes
import net.minecraft.nbt.NbtIo
import org.bukkit.craftbukkit.v1_20_R2.inventory.CraftItemStack
import org.bukkit.inventory.ItemStack
import java.io.Externalizable
import java.io.IOException
import java.io.ObjectInput
import java.io.ObjectOutput

object ItemStackCompanion
fun ItemStackCompanion.fromBytes(data: ByteArray): ItemStack {
	val nbtTagCompoundRoot = NbtIo.readCompressed(data.inputStream())
	val nmsItem = net.minecraft.world.item.ItemStack.of(nbtTagCompoundRoot)
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
