package com.fablesfantasyrp.plugin.discord

import com.fablesfantasyrp.plugin.utils.GLOBAL_SYSPREFIX
import com.fablesfantasyrp.plugin.utils.enforceDependencies
import dev.kord.common.entity.Snowflake
import org.bukkit.plugin.Plugin
import org.bukkit.plugin.java.JavaPlugin
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.context.loadKoinModules
import org.koin.core.context.unloadKoinModules
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.binds
import org.koin.dsl.module


internal val SYSPREFIX = GLOBAL_SYSPREFIX
internal val PLUGIN get() = FablesDiscord.instance

class FablesDiscord : JavaPlugin(), KoinComponent {
	private var koinModule: Module? = null

	override fun onEnable() {
		enforceDependencies(this)
		instance = this
		saveDefaultConfig()

		if (config.getString("token")!!.isEmpty()) {
			logger.warning("Discord bot token is not filled in config.yml, shutting down plugin.")
			isEnabled = false
			return
		}

		val botConfig = FablesDiscordBotConfig(
			token = config.getString("token")!!,
			nationDiscords = config.getStringList("nation_discords").map { Snowflake(it) }.toSet()
		)

		koinModule = module(createdAtStart = true) {
			single<Plugin> { this@FablesDiscord } binds(arrayOf(JavaPlugin::class))

			singleOf(::DiscordLinkingTracker) bind DiscordLinkService::class

			single { FablesDiscordBot(get(), botConfig) }
		}
		loadKoinModules(koinModule!!)

		get<DiscordLinkingTracker>().start()
		get<FablesDiscordBot>().start()

		if (server.pluginManager.isPluginEnabled("FablesWeb")) {
			logger.info("Enabling FablesWeb integration")
			com.fablesfantasyrp.plugin.discord.web.WebHook().start()
		}
	}

	override fun onDisable() {
		koinModule?.let { unloadKoinModules(it) }
	}

	companion object {
		lateinit var instance: FablesDiscord
			private set
	}
}
