package com.fablesfantasyrp.plugin.web

import org.bukkit.plugin.java.JavaPlugin


internal val ROLL_RANGE = 15U

class FablesRolls : JavaPlugin() {

	override fun onEnable() {
		instance = this


	}

	companion object {
		lateinit var instance: FablesRolls
	}
}
