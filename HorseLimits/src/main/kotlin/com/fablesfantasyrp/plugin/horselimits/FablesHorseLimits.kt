package com.fablesfantasyrp.plugin.horselimits

import org.bukkit.plugin.java.JavaPlugin

class FablesHorseLimits : JavaPlugin() {

	override fun onEnable() {
		server.pluginManager.registerEvents(HorseLimitsListener(), this)
	}
}
