package com.fablesfantasyrp.plugin.worldguardinterop

import com.fablesfantasyrp.plugin.utils.enforceDependencies
import org.bukkit.plugin.java.JavaPlugin

class FablesWorldGuardInterop : JavaPlugin() {
	override fun onEnable() {
		enforceDependencies(this)
	}
}
