package com.fablesfantasyrp.plugin.blanksystem

import com.fablesfantasyrp.plugin.utils.enforceDependencies
import org.bukkit.ChatColor.*
import org.bukkit.plugin.java.JavaPlugin


internal val SYSPREFIX = "$GOLD${BOLD}[${LIGHT_PURPLE}${BOLD} BLANK SYSTEM ${GOLD}${BOLD}]${GRAY}"
internal val PLUGIN get() = FablesBlankSystem.instance

class FablesBlankSystem : JavaPlugin() {

	override fun onEnable() {
		enforceDependencies(this)
		instance = this

		// everything else..
	}

	companion object {
		lateinit var instance: FablesBlankSystem
			private set
	}
}
