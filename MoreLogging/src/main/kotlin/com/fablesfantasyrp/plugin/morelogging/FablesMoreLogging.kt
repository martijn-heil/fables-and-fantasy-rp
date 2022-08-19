package com.fablesfantasyrp.plugin.morelogging

import com.fablesfantasyrp.plugin.utils.enforceDependencies
import org.bukkit.plugin.Plugin
import org.bukkit.plugin.java.JavaPlugin


class FablesMoreLogging : JavaPlugin() {

	override fun onEnable() {
		enforceDependencies(this)
		instance = this

		ModerationLoggerManager(this).start()

		MODERATION_LOGGER.info("Logging system starting up")

		server.pluginManager.registerEvents(BukkitListener(MODERATION_LOGGER, this), this)

		val essentials: Plugin? = server.pluginManager.getPlugin("Essentials")
		if (essentials != null) {
			server.pluginManager.registerEvents(EssentialsListener(MODERATION_LOGGER, this), this)
		}

		val superVanish: Plugin? = server.pluginManager.getPlugin("SuperVanish")
		if (superVanish != null) {
			server.pluginManager.registerEvents(SuperVanishListener(MODERATION_LOGGER, this), this)
		}
	}

	override fun onDisable() {
		MODERATION_LOGGER.info("Logging system shutting down")
	}

	companion object {
		lateinit var instance: FablesMoreLogging
	}
}
