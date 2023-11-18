package com.fablesfantasyrp.plugin.alternatemechanics.mechanic

import com.fablesfantasyrp.plugin.alternatemechanics.Mechanic
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.entity.EntityInteractEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.plugin.Plugin

class NoCropTrample(private val plugin: Plugin) : Mechanic {
	private val server = plugin.server

	override fun init() {
		server.pluginManager.registerEvents(NoCropTrampleListener(), plugin)
	}

	inner class NoCropTrampleListener : Listener {

		@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
		fun onPlayerInteract(e: PlayerInteractEvent) {
			if ((e.action == Action.PHYSICAL) && (e.clickedBlock?.type == Material.FARMLAND)) {
				e.isCancelled = true
			}
		}

		@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
		fun onEntityInteract(e: EntityInteractEvent) {
			if (e.block.type == Material.FARMLAND) {
				e.isCancelled = true
			}
		}
	}
}
