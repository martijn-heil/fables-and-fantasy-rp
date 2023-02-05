package com.fablesfantasyrp.plugin.utils

import com.earth2me.essentials.Essentials
import com.earth2me.essentials.spawn.EssentialsSpawn
import org.bukkit.Bukkit
import org.bukkit.Server
import org.bukkit.plugin.java.JavaPlugin
import org.koin.core.context.GlobalContext.startKoin
import org.koin.dsl.module

lateinit var essentials: Essentials
lateinit var essentialsSpawn: EssentialsSpawn

class FablesUtils : JavaPlugin() {

	override fun onEnable() {
		essentials = server.pluginManager.getPlugin("Essentials") as Essentials
		essentialsSpawn = server.pluginManager.getPlugin("EssentialsSpawn") as EssentialsSpawn

		Services.register(Server::class, server, this)

		val commonKoinModule = module {
			single { Bukkit.getServer() }
		}

		startKoin {
			modules(commonKoinModule)
		}
	}
}
