package com.fablesfantasyrp.plugin.discord

import com.fablesfantasyrp.plugin.utils.enforceDependencies
import org.bukkit.ChatColor.*
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


internal val SYSPREFIX = "$GOLD${BOLD}[${LIGHT_PURPLE}${BOLD} DISCORD ${GOLD}${BOLD}]${GRAY}"
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

		koinModule = module(createdAtStart = true) {
			single<Plugin> { this@FablesDiscord } binds(arrayOf(JavaPlugin::class))

			singleOf(::DiscordLinkingTracker) bind DiscordLinkService::class

			single { FablesDiscordBot(get(), config.getString("token")!!) }
		}
		loadKoinModules(koinModule!!)

		get<DiscordLinkingTracker>().start()
		get<FablesDiscordBot>().start()
	}

	override fun onDisable() {
		koinModule?.let { unloadKoinModules(it) }
	}

	companion object {
		lateinit var instance: FablesDiscord
			private set
	}
}
