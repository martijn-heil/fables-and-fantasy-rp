package com.fablesfantasyrp.plugin.whitelist

import com.fablesfantasyrp.plugin.utils.enforceDependencies
import org.bukkit.plugin.java.JavaPlugin

internal val PLUGIN: FablesWhitelist
	get() = FablesWhitelist.instance

class FablesWhitelist : JavaPlugin() {

	override fun onEnable() {
		enforceDependencies(this)
		instance = this
		server.pluginManager.registerEvents(WhitelistListener(this), this)
		WhitelistMonitor(this).start()
	}

	companion object {
		lateinit var instance: FablesWhitelist
	}
}
