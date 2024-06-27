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
package com.fablesfantasyrp.plugin.inventory.service

import com.fablesfantasyrp.plugin.inventory.domain.FablesInventory
import net.kyori.adventure.text.Component
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryHolder
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.Plugin
import java.util.*


class MirroredInventory(val fablesInventory: FablesInventory, owner: InventoryHolder, title: Component) {
	private var initialState = fablesInventory.map { it?.clone() }.toTypedArray()
	val bukkitInventory: Inventory = fablesInventory.asGenericInventoryCopy(owner, title)

	fun tick() {
		val currentState = bukkitInventory.contents!!.map { it?.clone() }.toTypedArray()
		initialState.mapIndexed { index, itemStack -> Pair(index, Pair(itemStack, currentState[index])) }.forEach {
			val index = it.first
			val initialItemStack = it.second.first
			val currentItemStack = it.second.second
			if (initialItemStack != currentItemStack) {
				fablesInventory[index] = currentItemStack
			}
		}

		val newSnapshot = fablesInventory.map { it?.clone() }.toTypedArray()

		val newContent = Array<ItemStack?>(bukkitInventory.size) { null }
		newSnapshot.forEachIndexed { index, it -> newContent[index] = it?.clone() }
		bukkitInventory.contents = newContent

		initialState = newSnapshot.map { it?.clone() }.toTypedArray()
	}
}

class MirroredInventoryManager(private val plugin: Plugin) {
	private val map = WeakHashMap<Inventory, MirroredInventory>()
	private var taskId: Int = 0

	private fun tick() {
		map.values.forEach { it.tick() }
	}

	fun start() {
		taskId = plugin.server.scheduler.scheduleSyncRepeatingTask(plugin, { tick() }, 0, 1)
		if (taskId == -1) throw Exception("Could not schedule task")
	}

	fun stop() {
		plugin.server.scheduler.cancelTask(taskId)
	}

	fun register(inventory: MirroredInventory) {
		map[inventory.bukkitInventory] = inventory
	}
}
