package com.fablesfantasyrp.plugin.inventory

import com.fablesfantasyrp.plugin.inventory.data.entity.FablesInventoryRepository
import com.fablesfantasyrp.plugin.profile.event.PlayerSwitchProfileEvent
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener

class ProfileInventoryListener(private val inventories: FablesInventoryRepository) : Listener {
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	fun onPlayerSwitchProfile(e: PlayerSwitchProfileEvent) {
		val old = e.old
		val new = e.new
		val transaction = e.transaction

		if (old != null) {
			transaction.setProperty(old.inventory.inventory::bukkitInventory, null)
			transaction.setProperty(old.inventory.enderChest::bukkitInventory, null)
		}

		if (new != null) {
			transaction.setProperty(new.inventory.inventory::bukkitInventory, e.player.inventory)
			transaction.setProperty(new.inventory.enderChest::bukkitInventory, e.player.enderChest)
		}
	}
}
