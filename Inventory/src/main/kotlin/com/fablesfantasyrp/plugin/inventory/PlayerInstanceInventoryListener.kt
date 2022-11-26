package com.fablesfantasyrp.plugin.inventory

import com.fablesfantasyrp.plugin.inventory.data.entity.FablesInventoryRepository
import com.fablesfantasyrp.plugin.playerinstance.PlayerSwitchPlayerInstanceEvent
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener

class PlayerInstanceInventoryListener(private val inventories: FablesInventoryRepository) : Listener {
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	fun onPlayerSwitchPlayerInstance(e: PlayerSwitchPlayerInstanceEvent) {
		val old = e.old
		val new = e.new

		if (old != null) {
			old.inventory.inventory.bukkitInventory = null
			old.inventory.enderChest.bukkitInventory = null
		}

		if (new != null) {
			new.inventory.inventory.bukkitInventory = e.player.inventory
			new.inventory.enderChest.bukkitInventory = e.player.enderChest
		}
	}
}
