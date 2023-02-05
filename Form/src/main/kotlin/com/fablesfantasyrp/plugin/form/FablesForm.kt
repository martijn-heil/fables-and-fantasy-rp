package com.fablesfantasyrp.plugin.form

import org.bukkit.plugin.java.JavaPlugin

internal val PLUGIN get() = FablesForm.instance

class FablesForm : JavaPlugin() {
	override fun onEnable() {
		instance = this

		this.server.pluginManager.registerEvents(ChatInputListener(), this)
		ClickableManager(this).start()
	}

	companion object {
		lateinit var instance: FablesForm
	}
}
