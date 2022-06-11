package com.fablesfantasyrp.plugin.form

import com.github.shynixn.mccoroutine.SuspendingJavaPlugin

internal val PLUGIN get() = FablesForm.instance

class FablesForm : SuspendingJavaPlugin() {
	override fun onEnable() {
		instance = this

		this.server.pluginManager.registerEvents(ChatInputListener(), this)
	}

	companion object {
		lateinit var instance: FablesForm
	}
}
