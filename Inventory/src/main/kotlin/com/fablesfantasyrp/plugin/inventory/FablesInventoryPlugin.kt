package com.fablesfantasyrp.plugin.inventory

import com.fablesfantasyrp.plugin.database.applyMigrations
import com.fablesfantasyrp.plugin.inventory.data.entity.EntityFablesInventoryRepository
import com.fablesfantasyrp.plugin.inventory.data.entity.FablesInventoryRepository
import com.fablesfantasyrp.plugin.inventory.data.persistent.H2ProfileInventoryRepository
import com.fablesfantasyrp.plugin.profile.ProfileManager
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

internal val PLUGIN get() = FablesInventoryPlugin.instance

class FablesInventoryPlugin : JavaPlugin(), KoinComponent {
	private lateinit var mirroredInventoryManager: MirroredInventoryManager
	private lateinit var inventories: EntityFablesInventoryRepository<*>
	private lateinit var profileManager: ProfileManager
	private lateinit var koinModule: Module

	override fun onEnable() {
		enforceDependencies(this)
		instance = this


		try {
			applyMigrations(this, "FABLES_INVENTORY", this.classLoader)
		} catch (e: Exception) {
			this.isEnabled = false
			return
		}

		koinModule = module(createdAtStart = true) {
			single<Plugin> { this@FablesInventoryPlugin } binds(arrayOf(JavaPlugin::class))

			single {
				val inventories = EntityFablesInventoryRepository(H2ProfileInventoryRepository(get()))
				inventories.init()
				inventories
			} bind FablesInventoryRepository::class

			single {
				val tmp = MirroredInventoryManager(get())
				tmp.start()
				tmp
			}

			singleOf(::ProfileInventoryListener)
		}
		loadKoinModules(koinModule)

		inventories = get()
		mirroredInventoryManager = get()
		profileManager = get()
		server.pluginManager.registerEvents(get<ProfileInventoryListener>(), this)

		server.scheduler.scheduleSyncRepeatingTask(this, {
			logger.info("Saving inventories..")
			inventories.saveAll()
		}, 0, 6000)
	}

	override fun onDisable() {
		server.onlinePlayers.forEach {
			val profile = profileManager.getCurrentForPlayer(it) ?: return@forEach
			profile.inventory.inventory.bukkitInventory = null
			profile.inventory.enderChest.bukkitInventory = null
		}
		inventories.saveAll()
		mirroredInventoryManager.stop()
		unloadKoinModules(koinModule)
	}

	companion object {
		lateinit var instance: FablesInventoryPlugin
	}
}
