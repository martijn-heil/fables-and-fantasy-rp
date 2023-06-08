package com.fablesfantasyrp.plugin.gui

import org.bukkit.plugin.java.JavaPlugin

internal val PLUGIN get() = FablesGui.instance

class FablesGui : JavaPlugin() {
	override fun onEnable() {
		instance = this
	}

	companion object {
		lateinit var instance: FablesGui
	}
}
