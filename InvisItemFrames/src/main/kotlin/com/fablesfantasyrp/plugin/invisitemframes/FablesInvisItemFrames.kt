package com.fablesfantasyrp.plugin.invisitemframes

import com.fablesfantasyrp.plugin.utils.enforceDependencies
import com.fablesfantasyrp.plugin.invisitemframes.listeners.ItemFrameListener
import org.bukkit.ChatColor.*
import org.bukkit.plugin.java.JavaPlugin


internal val SYSPREFIX = "$GOLD${BOLD}[${LIGHT_PURPLE}${BOLD} INVIS ITEMFRAMES ${GOLD}${BOLD}]${GRAY}"
internal val PLUGIN get() = FablesInvisItemFrames.instance

class FablesInvisItemFrames : JavaPlugin() {

	override fun onEnable() {
		enforceDependencies(this)
		instance = this

		server.pluginManager.registerEvents(ItemFrameListener(), this)
	}

	companion object {
		lateinit var instance: FablesInvisItemFrames
			private set
	}
}
