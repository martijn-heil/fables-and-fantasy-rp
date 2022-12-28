package com.fablesfantasyrp.plugin.leadbreakingsound.listeners

import org.bukkit.Sound
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityUnleashEvent

class LeadBreakingListener : Listener {

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	fun onPlayerBreaksLead(event: EntityUnleashEvent) {
		if (event.reason == EntityUnleashEvent.UnleashReason.DISTANCE ||
				event.reason == EntityUnleashEvent.UnleashReason.HOLDER_GONE) {
			val location = event.entity.location
			val world = location.world
			world.playSound(location, Sound.ENTITY_LEASH_KNOT_BREAK, 1f, 1f)
		}
	}
}
