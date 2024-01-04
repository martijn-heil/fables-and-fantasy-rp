package com.fablesfantasyrp.plugin.inventory

import com.fablesfantasyrp.plugin.inventory.domain.repository.ProfileInventoryRepository
import com.fablesfantasyrp.plugin.profile.event.PlayerSwitchProfileEvent
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener

class ProfileInventoryListener(private val inventories: ProfileInventoryRepository) : Listener {
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	fun onPlayerSwitchProfile(e: PlayerSwitchProfileEvent) {
		val old = e.old
		val new = e.new
		val transaction = e.transaction

		if (old != null) {
			val profileInventory = frunBlocking { inventories.forOwner(old) }

			transaction.setProperty(profileInventory.inventory::bukkitInventory, null)
			transaction.setProperty(profileInventory.enderChest::bukkitInventory, null)
		}

		if (new != null) {
			val profileInventory = frunBlocking { inventories.forOwner(new) }

			transaction.setProperty(profileInventory.inventory::bukkitInventory, e.player.inventory)
			transaction.setProperty(profileInventory.enderChest::bukkitInventory, e.player.enderChest)
		}
	}
}
