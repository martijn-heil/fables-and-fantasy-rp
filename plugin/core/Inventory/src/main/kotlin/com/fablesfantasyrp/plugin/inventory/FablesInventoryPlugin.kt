package com.fablesfantasyrp.plugin.inventory

import com.fablesfantasyrp.plugin.database.applyMigrations
import com.fablesfantasyrp.plugin.database.scheduleRepeatingDataSave
import com.fablesfantasyrp.plugin.inventory.appstart.KoinConfig
import com.fablesfantasyrp.plugin.inventory.domain.repository.ProfileInventoryRepositoryImpl
import com.fablesfantasyrp.plugin.inventory.service.MirroredInventoryManager
import com.fablesfantasyrp.plugin.profile.ProfileManager
import com.fablesfantasyrp.plugin.utils.enforceDependencies
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import org.bukkit.plugin.java.JavaPlugin
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

internal val PLUGIN get() = FablesInventoryPlugin.instance

class FablesInventoryPlugin : JavaPlugin(), KoinComponent {
	private lateinit var koinConfig: KoinConfig

	override fun onEnable() {
		enforceDependencies(this)
		instance = this


		try {
			applyMigrations(this, "FABLES_INVENTORY", this.classLoader)
		} catch (e: Exception) {
			this.isEnabled = false
			return
		}

		koinConfig = KoinConfig(this)
		koinConfig.load()

		get<ProfileInventoryRepositoryImpl>().init()
		get<MirroredInventoryManager>().start()

		server.pluginManager.registerEvents(get<ProfileInventoryListener>(), this)

		scheduleRepeatingDataSave(this) { get<ProfileInventoryRepositoryImpl>().saveAll() }
	}

	override fun onDisable() {
		val repository = get<ProfileInventoryRepositoryImpl>()
		frunBlocking {
			server.onlinePlayers.asFlow().onEach {
				val profile = get<ProfileManager>().getCurrentForPlayer(it) ?: return@onEach
				val profileInventory = repository.forOwner(profile)
				profileInventory.inventory.bukkitInventory = null
				profileInventory.enderChest.bukkitInventory = null
			}.collect()

			get<ProfileInventoryRepositoryImpl>().saveAll()
		}
		get<MirroredInventoryManager>().stop()
		koinConfig.unload()
	}

	companion object {
		lateinit var instance: FablesInventoryPlugin
	}
}
