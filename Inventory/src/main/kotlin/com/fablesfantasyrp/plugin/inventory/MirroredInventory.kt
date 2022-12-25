package com.fablesfantasyrp.plugin.inventory

import net.kyori.adventure.text.Component
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryHolder
import org.bukkit.plugin.Plugin
import java.util.*


class MirroredInventory(val fablesInventory: FablesInventory, owner: InventoryHolder, title: Component) {
	private var initialState = fablesInventory.map { it?.clone() }.toTypedArray()
	val bukkitInventory: Inventory = fablesInventory.asGenericInventoryCopy(owner, title)

	fun tick() {
		val currentState = bukkitInventory.contents.map { it?.clone() }.toTypedArray()
		initialState.mapIndexed { index, itemStack -> Pair(index, Pair(itemStack, currentState[index])) }.forEach {
			val index = it.first
			val initialItemStack = it.second.first
			val currentItemStack = it.second.second
			if (initialItemStack != currentItemStack) {
				fablesInventory[index] = currentItemStack
			}
		}

		val newSnapshot = fablesInventory.map { it?.clone() }.toTypedArray()
		bukkitInventory.contents = newSnapshot
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
