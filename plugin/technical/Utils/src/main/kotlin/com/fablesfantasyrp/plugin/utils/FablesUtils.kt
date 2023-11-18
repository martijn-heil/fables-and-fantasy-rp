package com.fablesfantasyrp.plugin.utils

import org.bukkit.Bukkit
import org.bukkit.Server
import org.bukkit.plugin.java.JavaPlugin
import org.koin.core.context.GlobalContext.startKoin
import org.koin.dsl.module


class FablesUtils : JavaPlugin() {

	override fun onEnable() {
		Services.register(Server::class, server, this)

		val commonKoinModule = module {
			single { Bukkit.getServer() }
		}

		startKoin {
			modules(commonKoinModule)
		}
	}
}
