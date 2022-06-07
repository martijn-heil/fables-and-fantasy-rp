package com.fablesfantasyrp.plugin.morelogging

import com.fablesfantasyrp.plugin.utils.enforceDependencies
import org.bukkit.plugin.Plugin
import org.bukkit.plugin.java.JavaPlugin


class FablesMoreLogging : JavaPlugin() {

	override fun onEnable() {
		enforceDependencies(this)
		instance = this

		server.pluginManager.registerEvents(BukkitListener(logger, this), this)

		val essentials: Plugin? = server.pluginManager.getPlugin("Essentials")
		if (essentials != null) {
			server.pluginManager.registerEvents(EssentialsListener(logger, this), this)
		}

		val superVanish: Plugin? = server.pluginManager.getPlugin("SuperVanish")
		if (superVanish != null) {
			server.pluginManager.registerEvents(SuperVanishListener(logger, this), this)
		}
	}

	companion object {
		lateinit var instance: FablesMoreLogging
	}
}
