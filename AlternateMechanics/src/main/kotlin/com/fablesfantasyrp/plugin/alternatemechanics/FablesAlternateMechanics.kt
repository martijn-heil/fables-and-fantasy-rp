package com.fablesfantasyrp.plugin.alternatemechanics

import com.fablesfantasyrp.plugin.utils.enforceDependencies
import org.bukkit.ChatColor.*
import org.bukkit.plugin.java.JavaPlugin


internal val SYSPREFIX = "$GOLD${BOLD}[${LIGHT_PURPLE}${BOLD} ALTERNATE MECHANICS ${GOLD}${BOLD}]${GRAY}"
internal val PLUGIN get() = FablesAlternateMechanics.instance

class FablesAlternateMechanics : JavaPlugin() {

	override fun onEnable() {
		enforceDependencies(this)
		instance = this

		server.pluginManager.registerEvents(AlternateMechanicsListener(), this)
	}

	companion object {
		lateinit var instance: FablesAlternateMechanics
			private set
	}
}
