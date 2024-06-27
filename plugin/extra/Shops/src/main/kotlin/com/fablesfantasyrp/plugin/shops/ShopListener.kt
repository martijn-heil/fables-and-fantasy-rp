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
package com.fablesfantasyrp.plugin.shops

import com.fablesfantasyrp.plugin.economy.data.entity.ProfileEconomyRepository
import com.fablesfantasyrp.plugin.profile.ProfileManager
import com.fablesfantasyrp.plugin.shops.domain.repository.ShopRepository
import com.fablesfantasyrp.plugin.shops.gui.ShopCustomerGui
import com.fablesfantasyrp.plugin.shops.gui.ShopVendorGui
import com.fablesfantasyrp.plugin.text.sendError
import com.fablesfantasyrp.plugin.utils.extensions.bukkit.toBlockIdentifier
import org.bukkit.Tag
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority.NORMAL
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.plugin.java.JavaPlugin

class ShopListener(private val plugin: JavaPlugin,
				   private val shops: ShopRepository,
				   private val profileManager: ProfileManager,
				   private val authorizer: ShopAuthorizer,
				   private val profileEconomyRepository: ProfileEconomyRepository) : Listener {
	@EventHandler(priority = NORMAL, ignoreCancelled = true)
	fun onPlayerRightClickShop(e: PlayerInteractEvent) {
		val player = e.player
		if (e.hand != EquipmentSlot.HAND) return
		val block = e.clickedBlock ?: return
		if (!Tag.SLABS.isTagged(block.type)) return
		val profile = profileManager.getCurrentForPlayer(player)
		val shop = shops.forLocation(block.location.toBlockIdentifier()) ?: return

		if (profile == null) {
			player.sendError("You must be on a profile to interact with a shop")
			return
		}
		e.isCancelled = true

		flaunch {
			val title = shop.displayTitle()
			if (authorizer.mayEdit(shop, profile)) {
				ShopVendorGui(plugin, title, shop, shops, profileManager, profileEconomyRepository).show(player)
			} else {
				ShopCustomerGui(plugin, title, shop, profileManager, profileEconomyRepository).show(player)
			}
		}
	}

	// TODO prevent block breaking of shop slabs
}
