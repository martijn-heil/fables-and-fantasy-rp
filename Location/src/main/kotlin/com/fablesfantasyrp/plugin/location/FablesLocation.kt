package com.fablesfantasyrp.plugin.location

import com.fablesfantasyrp.plugin.database.FablesDatabase.Companion.fablesDatabase
import com.fablesfantasyrp.plugin.database.applyMigrations
import com.fablesfantasyrp.plugin.location.data.entity.EntityProfileLocationRepository
import com.fablesfantasyrp.plugin.location.data.persistent.H2ProfileLocationRepository
import com.fablesfantasyrp.plugin.profile.profileManager
import com.fablesfantasyrp.plugin.utils.SPAWN
import com.fablesfantasyrp.plugin.utils.enforceDependencies
import com.github.shynixn.mccoroutine.bukkit.SuspendingJavaPlugin

internal val PLUGIN get() = FablesLocation.instance

class FablesLocation : SuspendingJavaPlugin() {
	lateinit var profileLocationRepository: EntityProfileLocationRepository<*>
		private set

	override fun onEnable() {
		enforceDependencies(this)
		instance = this


		try {
			applyMigrations(this, "FABLES_LOCATION", this.classLoader)
		} catch (e: Exception) {
			this.isEnabled = false
			return
		}

		profileLocationRepository = EntityProfileLocationRepository(
				H2ProfileLocationRepository(fablesDatabase, server, profileManager, SPAWN))
		profileLocationRepository.init()

		server.pluginManager.registerEvents(ProfileLocationListener(profileLocationRepository), this)
	}

	override fun onDisable() {
		server.onlinePlayers.forEach {
			val profile = profileManager.getCurrentForPlayer(it) ?: return@forEach
			profileLocationRepository.forOwner(profile).player = null
		}
		profileLocationRepository.saveAll()
	}

	companion object {
		lateinit var instance: FablesLocation
	}
}
