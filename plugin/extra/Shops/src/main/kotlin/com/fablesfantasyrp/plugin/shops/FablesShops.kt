package com.fablesfantasyrp.plugin.shops

import com.fablesfantasyrp.plugin.database.applyMigrations
import com.fablesfantasyrp.plugin.shops.appstart.CommandConfig
import com.fablesfantasyrp.plugin.shops.appstart.KoinConfig
import com.fablesfantasyrp.plugin.shops.domain.repository.ShopRepositoryImpl
import com.fablesfantasyrp.plugin.utils.GLOBAL_SYSPREFIX
import com.fablesfantasyrp.plugin.utils.enforceDependencies
import org.bukkit.plugin.java.JavaPlugin
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

internal val SYSPREFIX = GLOBAL_SYSPREFIX
internal val PLUGIN get() = FablesShops.instance

class FablesShops : JavaPlugin(), KoinComponent {
	private lateinit var koinConfig: KoinConfig

	override fun onEnable() {
		enforceDependencies(this)
		instance = this

		try {
			applyMigrations(this, "FABLES_SHOPS", this.classLoader)
		} catch (e: Exception) {
			this.isEnabled = false
			return
		}

		saveDefaultConfig()

		koinConfig = KoinConfig(this)
		koinConfig.load()

		get<ShopRepositoryImpl>().init()
		get<CommandConfig>().init()

		server.pluginManager.registerEvents(get<ShopListener>(), this)
	}

	override fun onDisable() {
		get<CommandConfig>().cleanup()
		get<ShopRepositoryImpl>().saveAllDirty()
		koinConfig.unload()
	}

	companion object {
		lateinit var instance: FablesShops
	}
}
