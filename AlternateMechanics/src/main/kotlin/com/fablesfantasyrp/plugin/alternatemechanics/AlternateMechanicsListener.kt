package com.fablesfantasyrp.plugin.alternatemechanics

import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority.NORMAL
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityResurrectEvent
import org.bukkit.event.player.PlayerItemConsumeEvent

class AlternateMechanicsListener : Listener {
	@EventHandler(priority = NORMAL, ignoreCancelled = true)
	fun onPlayerItemConsume(e: PlayerItemConsumeEvent) {
		if (setOf(Material.GOLDEN_APPLE, Material.ENCHANTED_GOLDEN_APPLE).contains(e.item.type)) {
			e.isCancelled = true
		}
	}

	// Cancel totem of undying effects
	@EventHandler(priority = NORMAL, ignoreCancelled = true)
	fun onPlayerResurrect(e: EntityResurrectEvent) {
		e.isCancelled = true
	}
}
