package com.fablesfantasyrp.plugin.utils

import com.earth2me.essentials.Essentials
import com.earth2me.essentials.spawn.EssentialsSpawn
import org.bukkit.plugin.java.JavaPlugin

lateinit var essentials: Essentials
lateinit var essentialsSpawn: EssentialsSpawn

class FablesUtils : JavaPlugin() {

	override fun onEnable() {
		essentials = server.pluginManager.getPlugin("Essentials") as Essentials
		essentialsSpawn = server.pluginManager.getPlugin("EssentialsSpawn") as EssentialsSpawn
	}
}
