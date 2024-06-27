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
