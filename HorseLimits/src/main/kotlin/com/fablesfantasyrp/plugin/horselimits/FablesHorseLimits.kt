package com.fablesfantasyrp.plugin.horselimits

import org.bukkit.entity.Horse
import org.bukkit.plugin.java.JavaPlugin

class FablesHorseLimits : JavaPlugin() {
	val MAX_JUMP_STRENGTH = 0.6

	override fun onEnable() {
		logger.info("horse limits enabled boi!")
		server.scheduler.scheduleSyncRepeatingTask(this, {
			val horses = server.worlds.map { it.entities.filterIsInstance<Horse>() }.flatten()
			horses.forEach { if(it.jumpStrength > MAX_JUMP_STRENGTH) it.jumpStrength = MAX_JUMP_STRENGTH }
		}, 1, 1)
	}
}
