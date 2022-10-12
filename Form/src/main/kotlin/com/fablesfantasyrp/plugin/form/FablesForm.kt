package com.fablesfantasyrp.plugin.form

import com.github.shynixn.mccoroutine.bukkit.SuspendingJavaPlugin

internal val PLUGIN get() = FablesForm.instance

class FablesForm : SuspendingJavaPlugin() {
	override fun onEnable() {
		instance = this

		this.server.pluginManager.registerEvents(ChatInputListener(), this)
		ClickableManager(this).start()
	}

	companion object {
		lateinit var instance: FablesForm
	}
}
