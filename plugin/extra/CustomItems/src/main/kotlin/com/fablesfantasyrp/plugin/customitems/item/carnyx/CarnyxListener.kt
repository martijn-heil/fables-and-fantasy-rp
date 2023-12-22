package com.fablesfantasyrp.plugin.customitems.item.carnyx

import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.plugin.java.JavaPlugin

class CarnyxListener(private val plugin: JavaPlugin) : Listener {
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = false)
	fun onPlayerUseCarnyx(e: PlayerInteractEvent) {
		if (e.action != Action.RIGHT_CLICK_AIR && e.action != Action.RIGHT_CLICK_BLOCK) return
		if (e.hand != EquipmentSlot.HAND) return
		val item = e.item ?: return
		if (!Carnyx.matches(item)) return

		CarnyxGui(plugin).show(e.player)
	}
}
