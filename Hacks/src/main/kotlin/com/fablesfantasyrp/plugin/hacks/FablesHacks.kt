package com.fablesfantasyrp.plugin.hacks

import net.milkbowl.vault.chat.Chat
import net.milkbowl.vault.permission.Permission
import org.bukkit.plugin.Plugin
import org.bukkit.plugin.ServicePriority
import org.bukkit.plugin.java.JavaPlugin
import org.koin.core.component.KoinComponent
import org.koin.core.context.loadKoinModules
import org.koin.core.context.unloadKoinModules
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.binds
import org.koin.dsl.module
import java.util.*

internal val NINJOH_NAME = "Ninjoh"
internal val NINJOH_UUID = UUID.fromString("50d8fcf0-166e-4ab3-9176-c41fb575071a")

class FablesHacks : JavaPlugin(), KoinComponent {
	private lateinit var koinModule: Module

	override fun onEnable() {
		val perms = server.servicesManager.getRegistration(Permission::class.java)!!
		val chat = server.servicesManager.getRegistration(Chat::class.java)!!

		server.servicesManager.register(Chat::class.java,
				HackyVaultChat(chat.provider, perms.provider), this, ServicePriority.Highest)

		koinModule = module(createdAtStart = false) {
			single<Plugin> { this@FablesHacks } binds(arrayOf(JavaPlugin::class))

			singleOf(::FlippedPlayerManager)
			singleOf(::HackyListener)
		}
		loadKoinModules(koinModule)

		/*server.scheduler.scheduleSyncDelayedTask(this, {
			get<FlippedPlayerManager>().start()
			server.pluginManager.registerEvents(get<HackyListener>(), this)
		}, 1)*/
	}

	override fun onDisable() {
		//get<FlippedPlayerManager>().stop()
		unloadKoinModules(koinModule)
	}
}
