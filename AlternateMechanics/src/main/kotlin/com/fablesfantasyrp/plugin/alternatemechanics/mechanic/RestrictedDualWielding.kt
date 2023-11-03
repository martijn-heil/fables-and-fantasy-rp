package com.fablesfantasyrp.plugin.alternatemechanics.mechanic

import com.fablesfantasyrp.plugin.alternatemechanics.Mechanic
import com.fablesfantasyrp.plugin.alternatemechanics.SYSPREFIX
import com.fablesfantasyrp.plugin.utils.extensions.bukkit.fancyName
import com.fablesfantasyrp.plugin.utils.extensions.bukkit.tryFakeDrop
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority.MONITOR
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityPickupItemEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.player.PlayerItemHeldEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.Plugin

class RestrictedDualWielding(private val plugin: Plugin): Mechanic {
	private val server = plugin.server
	private val materials = hashSetOf(
		Material.WOODEN_AXE,
		Material.STONE_AXE,
		Material.IRON_AXE,
		Material.GOLDEN_AXE,
		Material.DIAMOND_AXE,
		Material.NETHERITE_AXE,
		Material.BOW,
		Material.CROSSBOW,
		Material.SHIELD,
		Material.TRIDENT
	)

	override fun init() {
		server.pluginManager.registerEvents(RestrictedDualWieldingListener(), plugin)
	}

	private fun check(player: Player) {
		val inventory = player.inventory
		val itemInMainHand = inventory.itemInMainHand
		val itemInOffhand = inventory.itemInOffHand
		if (itemInMainHand.type == Material.AIR || itemInOffhand.type == Material.AIR) return

		if (materials.contains(itemInMainHand.type) && materials.contains(itemInOffhand.type)) {
			rejectOffHand(player)
			player.sendMessage("$SYSPREFIX You cannot dual wield ${itemInMainHand.fancyName} and ${itemInOffhand.fancyName} at the same time.")
		}
	}

	private fun rejectOffHand(player: Player) {
		val inventory = player.inventory
		val item = inventory.itemInOffHand
		inventory.setItemInOffHand(ItemStack(Material.DIRT, 64))
		try {
			val remainder = inventory.addItem(item).values

			val location = player.location
			val world = location.world

			remainder
				.filter { player.tryFakeDrop(it) }
				.forEach { world.dropItem(location.add(0.00, 1.00, 0.00), it) }
		} finally {
			inventory.setItemInOffHand(null)
		}
	}

	inner class RestrictedDualWieldingListener : Listener {
		@EventHandler(priority = MONITOR, ignoreCancelled = true)
		fun onInventoryClose(e: InventoryCloseEvent) {
			val player = e.player as? Player ?: return
			check(player)
		}

		@EventHandler(priority = MONITOR, ignoreCancelled = true)
		fun onPlayerItemHeld(e: PlayerItemHeldEvent) {
			check(e.player)
		}

		@EventHandler(priority = MONITOR, ignoreCancelled = true)
		fun onPlayerItemHeld(e: EntityPickupItemEvent) {
			val player = e.entity as? Player ?: return
			check(player)
		}
	}
}
