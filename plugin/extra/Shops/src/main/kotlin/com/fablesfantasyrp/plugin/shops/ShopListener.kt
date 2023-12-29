package com.fablesfantasyrp.plugin.shops

import com.fablesfantasyrp.plugin.profile.ProfileManager
import com.fablesfantasyrp.plugin.shops.domain.repository.ShopRepository
import com.fablesfantasyrp.plugin.utils.extensions.bukkit.toBlockIdentifier
import org.bukkit.Material
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
				   private val slotCountCalculator: ShopSlotCountCalculator) : Listener {
	@EventHandler(priority = NORMAL, ignoreCancelled = true)
	fun onPlayerRightClickShop(e: PlayerInteractEvent) {
		if (e.hand != EquipmentSlot.HAND) return
		if (e.item == null) return
		val block = e.clickedBlock ?: return
		if (block.type != Material.LODESTONE) return
		val shop = shops.forLocation(block.location.toBlockIdentifier()) ?: return
		e.isCancelled = true

		// TODO handle
	}
}
