package com.fablesfantasyrp.plugin.horselimits

import org.bukkit.entity.Horse
import org.bukkit.plugin.java.JavaPlugin

class FablesHorseLimits : JavaPlugin() {
	val MAX_JUMP_STRENGTH = 0.6

	override fun onEnable() {
		server.scheduler.scheduleSyncRepeatingTask(this, {
			server.worlds.asSequence()
					.map { it.entities.filterIsInstance<Horse>() }
					.flatten()
					.forEach { if(it.jumpStrength > MAX_JUMP_STRENGTH) it.jumpStrength = MAX_JUMP_STRENGTH }
		}, 1, 1)
	}
}
