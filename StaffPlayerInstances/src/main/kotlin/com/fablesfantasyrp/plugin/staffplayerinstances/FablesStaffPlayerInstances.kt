package com.fablesfantasyrp.plugin.staffplayerinstances

import com.fablesfantasyrp.plugin.database.FablesDatabase.Companion.fablesDatabase
import com.fablesfantasyrp.plugin.database.applyMigrations
import com.fablesfantasyrp.plugin.playerinstance.data.entity.EntityPlayerInstanceRepository
import com.fablesfantasyrp.plugin.staffplayerinstances.data.H2StaffPlayerInstanceRepository
import com.fablesfantasyrp.plugin.staffplayerinstances.data.StaffPlayerInstanceRepository
import com.fablesfantasyrp.plugin.utils.Services
import com.fablesfantasyrp.plugin.utils.enforceDependencies
import org.bukkit.plugin.java.JavaPlugin


internal val PLUGIN get() = FablesStaffPlayerInstances.instance

class FablesStaffPlayerInstances : JavaPlugin() {

	override fun onEnable() {
		enforceDependencies(this)
		instance = this

		try {
			applyMigrations(this, "FABLES_STAFFPLAYERINSTANCES", this.classLoader)
		} catch (e: Exception) {
			this.isEnabled = false
			return
		}

		val playerInstances = Services.get<EntityPlayerInstanceRepository>()
		val staffPlayerInstances = H2StaffPlayerInstanceRepository(server, fablesDatabase, playerInstances)
		Services.register(StaffPlayerInstanceRepository::class, staffPlayerInstances, this)

		var worldBoundPlayerInstancesHook: WorldBoundPlayerInstancesHook? = null

		if (server.pluginManager.isPluginEnabled("FablesWorldBoundPlayerInstances")) {
			worldBoundPlayerInstancesHook = com.fablesfantasyrp.plugin.staffplayerinstances.WorldBoundPlayerInstancesHookImpl()
		} else {
			logger.warning("Could not find FablesWorldBoundPlayerInstances, integration will not be activated.")
		}

		if (worldBoundPlayerInstancesHook != null) {
			staffPlayerInstances.all().forEach { worldBoundPlayerInstancesHook.allowToFlatroom(it) }
		}

		server.pluginManager.registerEvents(StaffPlayerInstancesListener(
				this,
				playerInstances,
				staffPlayerInstances,
				worldBoundPlayerInstancesHook), this)
	}

	companion object {
		lateinit var instance: FablesStaffPlayerInstances
			private set
	}
}
