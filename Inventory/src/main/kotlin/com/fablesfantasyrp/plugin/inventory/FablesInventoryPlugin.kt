package com.fablesfantasyrp.plugin.inventory

import com.fablesfantasyrp.plugin.database.FablesDatabase.Companion.fablesDatabase
import com.fablesfantasyrp.plugin.database.applyMigrations
import com.fablesfantasyrp.plugin.inventory.data.entity.EntityFablesInventoryRepository
import com.fablesfantasyrp.plugin.inventory.data.persistent.H2ProfileInventoryRepository
import com.fablesfantasyrp.plugin.profile.profileManager
import com.fablesfantasyrp.plugin.utils.enforceDependencies
import com.github.shynixn.mccoroutine.bukkit.SuspendingJavaPlugin

internal val PLUGIN get() = FablesInventoryPlugin.instance

class FablesInventoryPlugin : SuspendingJavaPlugin() {
	lateinit var mirroredInventoryManager: MirroredInventoryManager
	lateinit var inventories: EntityFablesInventoryRepository<*>
		private set

	override fun onEnable() {
		enforceDependencies(this)
		instance = this


		try {
			applyMigrations(this, "FABLES_INVENTORY", this.classLoader)
		} catch (e: Exception) {
			this.isEnabled = false
			return
		}

		inventories = EntityFablesInventoryRepository(H2ProfileInventoryRepository(fablesDatabase))
		inventories.init()

		server.pluginManager.registerEvents(ProfileInventoryListener(inventories), this)

		mirroredInventoryManager = MirroredInventoryManager(this)
		mirroredInventoryManager.start()
	}

	override fun onDisable() {
		server.onlinePlayers.forEach {
			val profile = profileManager.getCurrentForPlayer(it) ?: return@forEach
			profile.inventory.inventory.bukkitInventory = null
			profile.inventory.enderChest.bukkitInventory = null
		}
		inventories.saveAll()
		mirroredInventoryManager.stop()
	}

	companion object {
		lateinit var instance: FablesInventoryPlugin
	}
}
