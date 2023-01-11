package com.fablesfantasyrp.plugin.worldboundplayerinstances

import com.fablesfantasyrp.plugin.database.applyMigrations
import com.fablesfantasyrp.plugin.utils.enforceDependencies
import org.bukkit.ChatColor.*
import org.bukkit.plugin.java.JavaPlugin


internal val SYSPREFIX = "$GOLD${BOLD}[${LIGHT_PURPLE}${BOLD} WORLD BINDING ${GOLD}${BOLD}]${GRAY}"
internal val PLUGIN get() = FablesBlankSystem.instance

class FablesBlankSystem : JavaPlugin() {

	override fun onEnable() {
		enforceDependencies(this)
		instance = this

		try {
			applyMigrations(this, "FABLES_WORLDBOUNDPLAYERINSTANCES", this.classLoader)
		} catch (e: Exception) {
			this.isEnabled = false
			return
		}
	}

	companion object {
		lateinit var instance: FablesBlankSystem
			private set
	}
}
