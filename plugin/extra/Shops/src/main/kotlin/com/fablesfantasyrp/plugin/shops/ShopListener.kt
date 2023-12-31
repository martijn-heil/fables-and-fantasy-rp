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
			if (authorizer.mayEdit(shop, profile)) {
				ShopVendorGui(plugin, shop, shops, profileManager, profileEconomyRepository).show(player)
			} else {
				ShopCustomerGui(plugin, shop, profileManager, profileEconomyRepository).show(player)
			}
		}
	}
}
