package com.fablesfantasyrp.plugin.alternatemechanics.mechanic

import com.fablesfantasyrp.plugin.alternatemechanics.Mechanic
import org.bukkit.entity.Horse
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.plugin.Plugin
import org.spigotmc.event.entity.EntityMountEvent

class HorseJumpStrength(private val plugin: Plugin) : Mechanic {
	val MAX_JUMP_STRENGTH = 0.6

	private val server = plugin.server

	override fun init() {
		server.pluginManager.registerEvents(HorseJumpStrengthListener(), plugin)
	}

	inner class HorseJumpStrengthListener : Listener {
		@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
		fun onPlayerMountHorse(e: EntityMountEvent) {
			val horse = e.mount as? Horse ?: return
			if (horse.jumpStrength > MAX_JUMP_STRENGTH) {
				horse.jumpStrength = MAX_JUMP_STRENGTH
			}
		}
	}
}
