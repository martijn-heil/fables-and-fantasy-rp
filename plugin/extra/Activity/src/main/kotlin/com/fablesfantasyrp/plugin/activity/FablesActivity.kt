package com.fablesfantasyrp.plugin.activity

import com.fablesfantasyrp.plugin.activity.appstart.CommandConfig
import com.fablesfantasyrp.plugin.activity.appstart.KoinConfig
import com.fablesfantasyrp.plugin.activity.domain.repository.ActivityRegionRepository
import com.fablesfantasyrp.plugin.utils.GLOBAL_SYSPREFIX
import com.fablesfantasyrp.plugin.utils.enforceDependencies
import com.github.shynixn.mccoroutine.bukkit.launch
import org.bukkit.plugin.java.JavaPlugin
import org.koin.core.component.KoinComponent
import org.koin.core.component.get


internal val SYSPREFIX = GLOBAL_SYSPREFIX
internal val PLUGIN get() = FablesActivity.instance

class FablesActivity : JavaPlugin(), KoinComponent {
	private lateinit var koinConfig: KoinConfig

	override fun onEnable() {
		enforceDependencies(this)
		instance = this
		saveDefaultConfig()

		koinConfig = KoinConfig(this)
		koinConfig.load()

		launch { warnNotFoundRegions() }

		get<CommandConfig>().init()
	}

	override fun onDisable() {
		get<CommandConfig>().cleanup()
		koinConfig.unload()
	}

	private suspend fun warnNotFoundRegions() {
		get<ActivityRegionRepository>().all().filter { it.region == null }.forEach {
			logger.warning("Could not find WorldGuard region for activity region '${it.id}'")
		}
	}

	companion object {
		lateinit var instance: FablesActivity
			private set
	}
}
