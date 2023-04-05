package com.fablesfantasyrp.plugin.horselimits

import org.bukkit.entity.Horse
import org.bukkit.plugin.java.JavaPlugin
import java.util.stream.Collectors

class FablesHorseLimits : JavaPlugin() {
	val MAX_JUMP_STRENGTH = 0.6

	override fun onEnable() {
		server.scheduler.scheduleSyncRepeatingTask(this, {
			server.worlds.parallelStream()
				.flatMap { it.entities.parallelStream() }
				.filter { it is Horse && it.jumpStrength > MAX_JUMP_STRENGTH }
				.collect(Collectors.toList())
				.forEach { (it as Horse).jumpStrength = MAX_JUMP_STRENGTH }
		}, 1, 1)
	}
}
