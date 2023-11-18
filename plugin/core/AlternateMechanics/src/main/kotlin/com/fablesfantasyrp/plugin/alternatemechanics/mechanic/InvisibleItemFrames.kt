package com.fablesfantasyrp.plugin.alternatemechanics.mechanic

import com.fablesfantasyrp.plugin.alternatemechanics.Mechanic
import org.bukkit.Material
import org.bukkit.entity.ItemFrame
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.player.PlayerInteractEntityEvent
import org.bukkit.plugin.Plugin

class InvisibleItemFrames(private val plugin: Plugin) : Mechanic {
	private val server = plugin.server

	override fun init() {
		server.pluginManager.registerEvents(InvisibleItemFramesListener(), plugin)
	}

	inner class InvisibleItemFramesListener : Listener {
		@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
		fun onPlayerClicksFrame(e: PlayerInteractEntityEvent) {
			val itemFrame : ItemFrame = e.rightClicked as? ItemFrame ?: return
			if(e.player.inventory.itemInMainHand.type == Material.AIR) return
			if(!e.player.isSneaking) return

			itemFrame.isVisible = false
		}

		@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
		fun onPlayerPunchesFrame(e: EntityDamageByEntityEvent){
			val itemFrame : ItemFrame = e.entity as? ItemFrame ?: return
			itemFrame.isVisible = true
		}
	}
}
