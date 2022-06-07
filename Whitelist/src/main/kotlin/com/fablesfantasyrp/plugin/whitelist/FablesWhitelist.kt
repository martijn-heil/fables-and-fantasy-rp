package com.fablesfantasyrp.plugin.whitelist

import com.fablesfantasyrp.plugin.utils.enforceDependencies
import com.github.shynixn.mccoroutine.SuspendingJavaPlugin

internal val PLUGIN: FablesWhitelist
	get() = FablesWhitelist.instance

class FablesWhitelist : SuspendingJavaPlugin() {

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
