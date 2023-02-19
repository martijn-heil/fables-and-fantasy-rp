package com.fablesfantasyrp.plugin.tools

import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.plugin.Plugin
import java.util.*

class PowerToolManager(private val plugin: Plugin) {
	private val server = plugin.server
	private val powerTools: MutableMap<Player, MutableMap<Material, String>> = HashMap()

	fun start() {
		server.pluginManager.registerEvents(PowerToolListener(), plugin)
	}

	fun stop() {
		powerTools.clear()
	}

	fun setPowerTool(player: Player, material: Material, command: String?) {
		if (command != null) {
			powerTools
					.computeIfAbsent(player) { EnumMap(org.bukkit.Material::class.java) }
					.put(material, command)
		} else {
			powerTools.remove(player)
		}
	}

	fun getPowerTool(player: Player, material: Material): String? {
		return powerTools[player]?.get(material)
	}

	inner class PowerToolListener : Listener {
		@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
		fun onPlayerInteract(e: PlayerInteractEvent) {
			if (e.hand != EquipmentSlot.HAND) return
			val material = e.player.inventory.itemInMainHand.type
			val command = getPowerTool(e.player, material) ?: return
			e.isCancelled = true
			e.player.performCommand(command)
		}

		@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
		fun onPlayerQuit(e: PlayerQuitEvent) {
			powerTools.remove(e.player)
		}
	}
}
