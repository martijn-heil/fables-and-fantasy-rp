package com.fablesfantasyrp.plugin.shops

import com.fablesfantasyrp.plugin.database.applyMigrations
import com.fablesfantasyrp.plugin.database.scheduleRepeatingDataSave
import com.fablesfantasyrp.plugin.shops.appstart.CommandConfig
import com.fablesfantasyrp.plugin.shops.appstart.KoinConfig
import com.fablesfantasyrp.plugin.shops.domain.repository.ShopRepositoryImpl
import com.fablesfantasyrp.plugin.shops.service.DisplayItemServiceImpl
import com.fablesfantasyrp.plugin.utils.GLOBAL_SYSPREFIX
import com.fablesfantasyrp.plugin.utils.enforceDependencies
import com.google.common.base.Stopwatch
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

		koinConfig = KoinConfig(this)
		koinConfig.load()

		val shopRepository = get<ShopRepositoryImpl>()
		shopRepository.init()
		get<CommandConfig>().init()

		val displayItemService = get<DisplayItemServiceImpl>()
		displayItemService.start()

		server.pluginManager.registerEvents(get<ShopListener>(), this)

		scheduleRepeatingDataSave(this) { get<ShopRepositoryImpl>().saveAllDirty() }

		flaunch {
			logger.info("Spawning display items..")
			val stopwatch = Stopwatch.createStarted()
			shopRepository.all().forEach { displayItemService.spawnDisplayItem(it.location, it.item.asOne()) }
			stopwatch.stop()
			logger.info("Spawning display items done! Took ${stopwatch.elapsed().toMillis()}ms")
		}
	}

	override fun onDisable() {
		get<CommandConfig>().cleanup()
		get<DisplayItemServiceImpl>().stop()
		frunBlocking { get<ShopRepositoryImpl>().saveAllDirty() }
		koinConfig.unload()
	}

	companion object {
		lateinit var instance: FablesShops
	}
}
