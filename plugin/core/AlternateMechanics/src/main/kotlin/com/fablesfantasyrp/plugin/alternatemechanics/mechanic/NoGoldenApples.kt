package com.fablesfantasyrp.plugin.alternatemechanics.mechanic

import com.fablesfantasyrp.plugin.alternatemechanics.Mechanic
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerItemConsumeEvent
import org.bukkit.plugin.Plugin

class NoGoldenApples(private val plugin: Plugin) : Mechanic {
	private val server = plugin.server

	override fun init() {
		server.pluginManager.registerEvents(NoGoldenApplesListener(), plugin)
	}

	inner class NoGoldenApplesListener : Listener {
		@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
		fun onPlayerItemConsume(e: PlayerItemConsumeEvent) {
			if (setOf(Material.GOLDEN_APPLE, Material.ENCHANTED_GOLDEN_APPLE).contains(e.item.type)) {
				e.isCancelled = true
			}
		}
	}
}
