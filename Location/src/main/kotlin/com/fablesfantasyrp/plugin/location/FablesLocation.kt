package com.fablesfantasyrp.plugin.location

import com.fablesfantasyrp.plugin.database.FablesDatabase.Companion.fablesDatabase
import com.fablesfantasyrp.plugin.database.applyMigrations
import com.fablesfantasyrp.plugin.location.data.entity.EntityPlayerInstanceLocationRepository
import com.fablesfantasyrp.plugin.location.data.persistent.H2PlayerInstanceLocationRepository
import com.fablesfantasyrp.plugin.playerinstance.currentPlayerInstance
import com.fablesfantasyrp.plugin.playerinstance.playerInstanceManager
import com.fablesfantasyrp.plugin.utils.enforceDependencies
import com.fablesfantasyrp.plugin.utils.essentialsSpawn
import com.github.shynixn.mccoroutine.bukkit.SuspendingJavaPlugin
import org.bukkit.Location

internal val PLUGIN get() = FablesLocation.instance

class FablesLocation : SuspendingJavaPlugin() {
	lateinit var playerInstanceLocationRepository: EntityPlayerInstanceLocationRepository<*>
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

		val defaultLocation: Location = essentialsSpawn.getSpawn("default")

		playerInstanceLocationRepository = EntityPlayerInstanceLocationRepository(
				H2PlayerInstanceLocationRepository(fablesDatabase, server, playerInstanceManager, defaultLocation))

		server.pluginManager.registerEvents(PlayerInstanceLocationListener(playerInstanceLocationRepository), this)
	}

	override fun onDisable() {
		server.onlinePlayers.forEach {
			val instance = it.currentPlayerInstance ?: return@forEach
			playerInstanceLocationRepository.forOwner(instance).player = null
		}
		playerInstanceLocationRepository.saveAll()
	}

	companion object {
		lateinit var instance: FablesLocation
	}
}
