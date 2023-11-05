package com.fablesfantasyrp.plugin.item

import com.fablesfantasyrp.plugin.utils.enforceDependencies
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


internal val PLUGIN get() = FablesItem.instance

class FablesItem : JavaPlugin(), KoinComponent {
	private lateinit var koinModule: Module

	override fun onEnable() {
		enforceDependencies(this)
		instance = this

		koinModule = module(createdAtStart = true) {
			single<Plugin> { this@FablesItem } binds (arrayOf(JavaPlugin::class))
			singleOf(::ItemTraitServiceImpl) bind ItemTraitService::class
			singleOf(::CursorItemOriginTracker) bind CursorItemOriginService::class
			singleOf(::SpaghettiListener)
		}
		loadKoinModules(koinModule)

		server.pluginManager.registerEvents(get<SpaghettiListener>(), this)
	}

	override fun onDisable() {
		unloadKoinModules(koinModule)
	}

	companion object {
		lateinit var instance: FablesItem
			private set
	}
}
