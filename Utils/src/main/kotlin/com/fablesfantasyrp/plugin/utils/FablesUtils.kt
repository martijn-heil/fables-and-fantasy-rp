package com.fablesfantasyrp.plugin.utils

import com.earth2me.essentials.Essentials
import org.bukkit.plugin.java.JavaPlugin

lateinit var essentials: Essentials

class FablesUtils : JavaPlugin() {

	override fun onEnable() {
		essentials = server.pluginManager.getPlugin("Essentials") as Essentials
	}
}
