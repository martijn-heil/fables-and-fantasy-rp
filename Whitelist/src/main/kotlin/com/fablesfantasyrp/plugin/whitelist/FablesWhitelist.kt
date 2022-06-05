package com.fablesfantasyrp.plugin.whitelist

import com.github.shynixn.mccoroutine.SuspendingJavaPlugin

internal val PLUGIN: FablesWhitelist
	get() = FablesWhitelist.instance

class FablesWhitelist : SuspendingJavaPlugin() {

	override fun onEnable() {
		instance = this
		server.pluginManager.registerEvents(WhitelistListener(server), this)
		WhitelistMonitor(this).start()
	}

	companion object {
		lateinit var instance: FablesWhitelist
	}
}
