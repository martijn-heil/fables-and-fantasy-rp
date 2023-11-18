package com.fablesfantasyrp.plugin.irondoors

import com.fablesfantasyrp.plugin.utils.enforceDependencies
import org.bukkit.plugin.java.JavaPlugin

internal val PLUGIN: FablesIronDoors
	get() = FablesIronDoors.instance

class FablesIronDoors : JavaPlugin() {

	override fun onEnable() {
		enforceDependencies(this)
		instance = this
		server.pluginManager.registerEvents(IronDoorsListener(), this)
	}

	companion object {
		lateinit var instance: FablesIronDoors

	}
}
