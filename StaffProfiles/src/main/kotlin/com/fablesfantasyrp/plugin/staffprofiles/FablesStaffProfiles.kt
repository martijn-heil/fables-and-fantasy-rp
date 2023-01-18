package com.fablesfantasyrp.plugin.staffprofiles

import com.fablesfantasyrp.plugin.database.FablesDatabase.Companion.fablesDatabase
import com.fablesfantasyrp.plugin.database.applyMigrations
import com.fablesfantasyrp.plugin.profile.data.entity.EntityProfileRepository
import com.fablesfantasyrp.plugin.staffprofiles.data.H2StaffProfileRepository
import com.fablesfantasyrp.plugin.staffprofiles.data.StaffProfileRepository
import com.fablesfantasyrp.plugin.utils.Services
import com.fablesfantasyrp.plugin.utils.enforceDependencies
import org.bukkit.plugin.java.JavaPlugin


internal val PLUGIN get() = FablesStaffProfiles.instance

class FablesStaffProfiles : JavaPlugin() {

	override fun onEnable() {
		enforceDependencies(this)
		instance = this

		try {
			applyMigrations(this, "FABLES_STAFFPROFILES", this.classLoader)
		} catch (e: Exception) {
			this.isEnabled = false
			return
		}

		val profiles = Services.get<EntityProfileRepository>()
		val staffProfiles = H2StaffProfileRepository(server, fablesDatabase, profiles)
		Services.register(StaffProfileRepository::class, staffProfiles, this)

		var worldBoundProfilesHook: WorldBoundProfilesHook? = null

		if (server.pluginManager.isPluginEnabled("FablesWorldBoundProfiles")) {
			worldBoundProfilesHook = com.fablesfantasyrp.plugin.staffprofiles.WorldBoundProfilesHookImpl()
		} else {
			logger.warning("Could not find FablesWorldBoundProfiles, integration will not be activated.")
		}

		if (worldBoundProfilesHook != null) {
			staffProfiles.all().forEach { worldBoundProfilesHook.allowToFlatroom(it) }
		}

		server.pluginManager.registerEvents(StaffProfilesListener(
				this,
				profiles,
				staffProfiles,
				worldBoundProfilesHook), this)
	}

	companion object {
		lateinit var instance: FablesStaffProfiles
			private set
	}
}
