package com.fablesfantasyrp.plugin.worldboundplayerinstances

import com.fablesfantasyrp.plugin.database.FablesDatabase.Companion.fablesDatabase
import com.fablesfantasyrp.plugin.database.applyMigrations
import com.fablesfantasyrp.plugin.playerinstance.PlayerInstanceManager
import com.fablesfantasyrp.plugin.playerinstance.data.entity.EntityPlayerInstanceRepository
import com.fablesfantasyrp.plugin.utils.Services
import com.fablesfantasyrp.plugin.utils.enforceDependencies
import com.fablesfantasyrp.plugin.worldboundplayerinstances.data.H2WorldRestrictionRuleRepository
import com.fablesfantasyrp.plugin.worldboundplayerinstances.data.WorldRestrictionRuleRepository
import org.bukkit.ChatColor.*
import org.bukkit.plugin.java.JavaPlugin


internal val SYSPREFIX = "$GOLD${BOLD}[${LIGHT_PURPLE}${BOLD} WORLD BINDING ${GOLD}${BOLD}]${GRAY}"
internal val PLUGIN get() = FablesWorldBoundPlayerInstances.instance

class FablesWorldBoundPlayerInstances : JavaPlugin() {

	override fun onEnable() {
		enforceDependencies(this)
		instance = this

		try {
			applyMigrations(this, "FABLES_WORLDBOUNDPLAYERINSTANCES", this.classLoader)
		} catch (e: Exception) {
			this.isEnabled = false
			return
		}

		val playerInstanceRepository = Services.get<EntityPlayerInstanceRepository>()
		val playerInstanceManager = Services.get<PlayerInstanceManager>()
		val worldRestrictionRuleRepository = H2WorldRestrictionRuleRepository(server, fablesDatabase, playerInstanceRepository)
		Services.register(WorldRestrictionRuleRepository::class, worldRestrictionRuleRepository, this)

		server.pluginManager.registerEvents(WorldBoundPlayerInstancesListener(
				this,
				playerInstanceRepository,
				playerInstanceManager,
				worldRestrictionRuleRepository), this
		)
	}

	companion object {
		lateinit var instance: FablesWorldBoundPlayerInstances
			private set
	}
}
