package com.fablesfantasyrp.plugin.invisitemframes.listeners


import org.bukkit.Material
import org.bukkit.entity.ItemFrame
import org.bukkit.event.EventPriority
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.player.PlayerInteractEntityEvent

class ItemFrameListener : Listener {

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	fun onPlayerClicksFrame(e: PlayerInteractEntityEvent) {
		val itemFrame : ItemFrame = e.rightClicked as? ItemFrame ?: return

		if(e.player.inventory.itemInMainHand.type == Material.AIR) return

		if(e.player.isSneaking){
			itemFrame.isVisible = false
		}

	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	fun onPlayerPunchesFrame(e: EntityDamageByEntityEvent){
		val itemFrame : ItemFrame = e.entity as? ItemFrame ?: return

		itemFrame.isVisible = true
	}

}
