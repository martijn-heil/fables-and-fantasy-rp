package com.fablesfantasyrp.plugin.weights

import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority.NORMAL
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityPickupItemEvent
import org.bukkit.plugin.Plugin
import java.util.*

class ItemPickupManager(private val plugin: Plugin) {
	private val server = plugin.server
	private val pickupDisabled = HashSet<UUID>()

	fun start() {
		server.pluginManager.registerEvents(ItemPickupManagerListener(), plugin)
	}

	fun setPickupDisabled(player: Player, value: Boolean)
		= if (value) pickupDisabled.add(player.uniqueId) else pickupDisabled.remove(player.uniqueId)

	fun hasPickupDisabled(player: Player) = pickupDisabled.contains(player.uniqueId)

	private inner class ItemPickupManagerListener : Listener {
		@EventHandler(priority = NORMAL, ignoreCancelled = true)
		fun onPlayerPickupItem(e: EntityPickupItemEvent) {
			val player = e.entity as? Player ?: return
			if (hasPickupDisabled(player)) {
				e.isCancelled = true
			}
		}
	}
}
