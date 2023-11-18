package com.fablesfantasyrp.plugin.alternatemechanics.mechanic

import com.fablesfantasyrp.plugin.alternatemechanics.Mechanic
import org.bukkit.Sound
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityUnleashEvent
import org.bukkit.plugin.Plugin

class LeadBreakSound(private val plugin: Plugin) : Mechanic {
	private val server = plugin.server

	override fun init() {
		server.pluginManager.registerEvents(LeadBreakSoundListener(), plugin)
	}

	inner class LeadBreakSoundListener : Listener {
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
}
