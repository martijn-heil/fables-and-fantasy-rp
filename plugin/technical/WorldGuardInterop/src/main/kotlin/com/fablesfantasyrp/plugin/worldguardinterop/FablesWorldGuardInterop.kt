package com.fablesfantasyrp.plugin.worldguardinterop

import com.fablesfantasyrp.plugin.utils.enforceDependencies
import com.sk89q.worldguard.WorldGuard
import org.bukkit.plugin.java.JavaPlugin
import org.koin.core.component.KoinComponent
import org.koin.core.context.loadKoinModules
import org.koin.core.context.unloadKoinModules
import org.koin.core.module.Module
import org.koin.dsl.module

class FablesWorldGuardInterop : JavaPlugin(), KoinComponent {
	private lateinit var koinModule: Module

	override fun onEnable() {
		enforceDependencies(this)

		koinModule = module(createdAtStart = true) {
			single { WorldGuard.getInstance().platform.regionContainer }
		}
		loadKoinModules(koinModule)
	}

	override fun onDisable() {
		unloadKoinModules(koinModule)
	}
}
