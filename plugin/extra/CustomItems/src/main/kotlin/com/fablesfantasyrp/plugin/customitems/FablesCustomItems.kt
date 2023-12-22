package com.fablesfantasyrp.plugin.customitems

import com.fablesfantasyrp.plugin.customitems.item.carnyx.CarnyxListener
import com.fablesfantasyrp.plugin.utils.GLOBAL_SYSPREFIX
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
import org.koin.dsl.binds
import org.koin.dsl.module

internal val SYSPREFIX = GLOBAL_SYSPREFIX
internal val PLUGIN get() = FablesCustomItems.instance

class FablesCustomItems : JavaPlugin(), KoinComponent {
	private lateinit var koinModule: Module

	override fun onEnable() {
		enforceDependencies(this)
		instance = this

		koinModule = module(true) {
			single<Plugin> { this@FablesCustomItems } binds (arrayOf(JavaPlugin::class))

			singleOf(::CarnyxListener)
		}
		loadKoinModules(koinModule)

		server.pluginManager.registerEvents(get<CarnyxListener>(), this)

		// everything else..
	}

	override fun onDisable() {
		unloadKoinModules(koinModule)
	}

	companion object {
		lateinit var instance: FablesCustomItems
			private set
	}
}
