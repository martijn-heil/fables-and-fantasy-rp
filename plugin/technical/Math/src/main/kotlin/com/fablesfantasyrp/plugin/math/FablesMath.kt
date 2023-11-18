package com.fablesfantasyrp.plugin.math

import org.bukkit.plugin.java.JavaPlugin

class FablesMath : JavaPlugin() {

	override fun onEnable() {
		instance = this
	}

	companion object {
		lateinit var instance: FablesMath
	}
}
