package com.fablesfantasyrp.plugin.tools

import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.plugin.Plugin

class PowerToolManager(private val plugin: Plugin) {
	private val server = plugin.server
	private val powerTools: MutableMap<Player, String> = HashMap()

	fun start() {
		server.pluginManager.registerEvents(PowerToolListener(), plugin)
	}

	fun stop() {
		powerTools.clear()
	}

	fun setPowerTool(player: Player, command: String?) {
		if (command != null) {
			powerTools[player] = command
		} else {
			powerTools.remove(player)
		}
	}

	inner class PowerToolListener : Listener {
		@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
		fun onPlayerInteract(e: PlayerInteractEvent) {
			if (e.hand != EquipmentSlot.HAND) return
			val command = powerTools[e.player] ?: return

			e.isCancelled = true
			e.player.performCommand(command)
		}

		@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
		fun onPlayerQuit(e: PlayerQuitEvent) {
			powerTools.remove(e.player)
		}
	}
}
