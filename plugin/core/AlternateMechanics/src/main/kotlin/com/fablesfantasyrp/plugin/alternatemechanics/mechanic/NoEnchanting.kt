package com.fablesfantasyrp.plugin.alternatemechanics.mechanic

import com.fablesfantasyrp.plugin.alternatemechanics.Mechanic
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityPickupItemEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.plugin.Plugin

class NoEnchanting(private val plugin: Plugin) : Mechanic {
	private val server = plugin.server

	override fun init() {
		server.pluginManager.registerEvents(NoEnchantingListener(), plugin)
	}

	inner class NoEnchantingListener : Listener {
		@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
		fun onPlayerInteract(e: PlayerInteractEvent) {
			val clickedBlock = e.clickedBlock ?: return
			if (clickedBlock.type == Material.ENCHANTING_TABLE) {
				e.isCancelled = true
			}
		}

		@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
		fun onPlayerItemPickup(e: EntityPickupItemEvent) {
			if (e.item.itemStack.enchantments.isNotEmpty()) {
				e.item.itemStack.enchantments.keys.forEach { e.item.itemStack.removeEnchantment(it) }
			}
		}
	}
}
