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
