package com.fablesfantasyrp.plugin.glowing

import com.fablesfantasyrp.plugin.utils.enforceDependencies
import me.neznamy.tab.api.TabAPI
import org.bukkit.ChatColor.*
import org.bukkit.plugin.Plugin
import org.bukkit.plugin.java.JavaPlugin
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.context.loadKoinModules
import org.koin.core.context.unloadKoinModules
import org.koin.core.module.Module
import org.koin.dsl.bind
import org.koin.dsl.binds
import org.koin.dsl.module


val SYSPREFIX = "${DARK_RED}${BOLD}[${RED}${BOLD} GLOWING ${DARK_RED}${BOLD}]${GRAY}"


class FablesGlowing : JavaPlugin(), KoinComponent {
	private lateinit var koinModule: Module
	private lateinit var denizenGlowingManager: DenizenGlowingManager
	val glowingManager: GlowingManager
		get() = denizenGlowingManager

	override fun onEnable() {
		enforceDependencies(this)
		instance = this

		koinModule = module(createdAtStart = true) {
			single<Plugin> { this@FablesGlowing } binds(arrayOf(JavaPlugin::class))
			single {
				val tmp = DenizenGlowingManager(get(), get())
				tmp.start()
				tmp
			} bind GlowingManager::class
			single { TabAPI.getInstance() }
		}
		loadKoinModules(koinModule)

		denizenGlowingManager = get<DenizenGlowingManager>()
	}

	override fun onDisable() {
		denizenGlowingManager.stop()
		unloadKoinModules(koinModule)
	}

	companion object {
		lateinit var instance: FablesGlowing
	}
}
