package com.fablesfantasyrp.plugin.horselimits

import org.bukkit.entity.Horse
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority.MONITOR
import org.bukkit.event.Listener
import org.spigotmc.event.entity.EntityMountEvent

class HorseLimitsListener : Listener {
	val MAX_JUMP_STRENGTH = 0.6

	@EventHandler(priority = MONITOR, ignoreCancelled = true)
	fun onPlayerMountHorse(e: EntityMountEvent) {
		val horse = e.mount as? Horse ?: return
		if (horse.jumpStrength > MAX_JUMP_STRENGTH) {
			horse.jumpStrength = MAX_JUMP_STRENGTH
		}
	}
}
