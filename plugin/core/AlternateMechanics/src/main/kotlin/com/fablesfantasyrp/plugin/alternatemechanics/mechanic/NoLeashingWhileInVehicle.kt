package com.fablesfantasyrp.plugin.alternatemechanics.mechanic

import com.fablesfantasyrp.plugin.alternatemechanics.Mechanic
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.PlayerLeashEntityEvent
import org.bukkit.event.vehicle.VehicleEnterEvent
import org.bukkit.plugin.Plugin

class NoLeashingWhileInVehicle(private val plugin: Plugin) : Mechanic {
	private val server = plugin.server

	override fun init() {
		server.pluginManager.registerEvents(NoLeashingWhileInVehicleListener(), plugin)
	}

	inner class NoLeashingWhileInVehicleListener : Listener {
		@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
		fun onPlayerLeashEntity(e: PlayerLeashEntityEvent) {
			if (e.player.isInsideVehicle) {
				e.player.sendRichMessage("<red>You can't leash entities while in a vehicle.")
				e.isCancelled = true
			}
		}

		@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
		fun onPlayerEnterVehicle(e: VehicleEnterEvent) {
			val player = e.entered as? Player ?: return
			val location = player.location
			location.getNearbyLivingEntities(30.0)
				.filter { it.isLeashed && it.leashHolder == player }
				.forEach {
					it.setLeashHolder(null)
					location.world.playSound(location, Sound.ENTITY_LEASH_KNOT_BREAK, 1f, 1f)
				}
		}
	}
}
