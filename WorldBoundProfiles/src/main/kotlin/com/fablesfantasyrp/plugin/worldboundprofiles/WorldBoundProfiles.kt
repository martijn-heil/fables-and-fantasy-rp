package com.fablesfantasyrp.plugin.worldboundprofiles

import com.fablesfantasyrp.plugin.database.FablesDatabase.Companion.fablesDatabase
import com.fablesfantasyrp.plugin.database.applyMigrations
import com.fablesfantasyrp.plugin.profile.ProfileManager
import com.fablesfantasyrp.plugin.profile.data.entity.EntityProfileRepository
import com.fablesfantasyrp.plugin.utils.Services
import com.fablesfantasyrp.plugin.utils.enforceDependencies
import com.fablesfantasyrp.plugin.worldboundprofiles.data.H2WorldRestrictionRuleRepository
import com.fablesfantasyrp.plugin.worldboundprofiles.data.WorldRestrictionRuleRepository
import org.bukkit.ChatColor.*
import org.bukkit.plugin.java.JavaPlugin


internal val SYSPREFIX = "$GOLD${BOLD}[${LIGHT_PURPLE}${BOLD} WORLD BINDING ${GOLD}${BOLD}]${GRAY}"
internal val PLUGIN get() = FablesWorldBoundProfiles.instance

class FablesWorldBoundProfiles : JavaPlugin() {

	override fun onEnable() {
		enforceDependencies(this)
		instance = this

		try {
			applyMigrations(this, "FABLES_WORLDBOUNDPROFILES", this.classLoader)
		} catch (e: Exception) {
			this.isEnabled = false
			return
		}

		val profiles = Services.get<EntityProfileRepository>()
		val profileManager = Services.get<ProfileManager>()
		val worldRestrictionRuleRepository = H2WorldRestrictionRuleRepository(server, fablesDatabase, profiles)
		Services.register(WorldRestrictionRuleRepository::class, worldRestrictionRuleRepository, this)

		server.pluginManager.registerEvents(WorldBoundProfilesListener(
				this,
				profiles,
				profileManager,
				worldRestrictionRuleRepository), this
		)
	}

	companion object {
		lateinit var instance: FablesWorldBoundProfiles
			private set
	}
}
