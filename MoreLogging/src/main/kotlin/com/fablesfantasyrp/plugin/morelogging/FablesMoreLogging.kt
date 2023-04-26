package com.fablesfantasyrp.plugin.morelogging

import com.fablesfantasyrp.plugin.utils.enforceDependencies
import org.bukkit.plugin.java.JavaPlugin
import org.koin.core.component.KoinComponent
import org.koin.core.context.loadKoinModules
import org.koin.core.context.unloadKoinModules
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module


class FablesMoreLogging : JavaPlugin(), KoinComponent {
	private lateinit var koinModule: Module

	override fun onEnable() {
		enforceDependencies(this)
		instance = this

		koinModule = module(createdAtStart = true) {
			singleOf(::StaffActionBroadcasterImpl) bind StaffActionBroadcaster::class
		}
		loadKoinModules(koinModule)

		ModerationLoggerManager(this).start()

		MODERATION_LOGGER.info("Logging system starting up")

		server.pluginManager.registerEvents(BukkitListener(MODERATION_LOGGER, this), this)

		if (server.pluginManager.isPluginEnabled("Essentials")) {
			server.pluginManager.registerEvents(EssentialsListener(MODERATION_LOGGER, this), this)
		}

		if (server.pluginManager.isPluginEnabled("SuperVanish")) {
			server.pluginManager.registerEvents(SuperVanishListener(MODERATION_LOGGER, this), this)
		}
	}

	override fun onDisable() {
		MODERATION_LOGGER.info("Logging system shutting down")
		unloadKoinModules(koinModule)
	}

	companion object {
		lateinit var instance: FablesMoreLogging
	}
}
