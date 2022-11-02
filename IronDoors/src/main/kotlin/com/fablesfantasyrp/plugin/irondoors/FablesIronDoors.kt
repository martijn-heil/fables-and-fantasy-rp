package com.fablesfantasyrp.plugin.irondoors

import com.fablesfantasyrp.plugin.utils.enforceDependencies
import com.github.shynixn.mccoroutine.bukkit.SuspendingJavaPlugin
import org.bukkit.Bukkit

internal val PLUGIN: FablesIronDoors
	get() = FablesIronDoors.instance

class FablesIronDoors : SuspendingJavaPlugin() {

	override fun onEnable() {
		enforceDependencies(this)
		instance = this
		server.pluginManager.registerEvents(IronDoorsListener(this), this)
	}

	companion object {
		lateinit var instance: FablesIronDoors

	}
}
