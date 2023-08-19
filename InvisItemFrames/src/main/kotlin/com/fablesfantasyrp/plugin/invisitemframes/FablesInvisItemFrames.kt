package com.fablesfantasyrp.plugin.invisitemframes

import com.fablesfantasyrp.plugin.invisitemframes.listeners.ItemFrameListener
import com.fablesfantasyrp.plugin.utils.GLOBAL_SYSPREFIX
import com.fablesfantasyrp.plugin.utils.enforceDependencies
import org.bukkit.plugin.java.JavaPlugin


internal val SYSPREFIX = GLOBAL_SYSPREFIX
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
