/*
 * Fables and Fantasy RP kotlin plugins.
 * Copyright (C) 2024  Martijn Heil
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.fablesfantasyrp.plugin.worldboundprofiles

import com.fablesfantasyrp.plugin.database.FablesDatabase.Companion.fablesDatabase
import com.fablesfantasyrp.plugin.database.applyMigrations
import com.fablesfantasyrp.plugin.profile.ProfileManager
import com.fablesfantasyrp.plugin.profile.data.entity.EntityProfileRepository
import com.fablesfantasyrp.plugin.utils.GLOBAL_SYSPREFIX
import com.fablesfantasyrp.plugin.utils.Services
import com.fablesfantasyrp.plugin.utils.enforceDependencies
import com.fablesfantasyrp.plugin.worldboundprofiles.data.H2WorldRestrictionRuleRepository
import com.fablesfantasyrp.plugin.worldboundprofiles.data.WorldRestrictionRuleRepository
import org.bukkit.plugin.java.JavaPlugin


internal val SYSPREFIX = GLOBAL_SYSPREFIX
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
		val worldRestrictionRuleRepository = H2WorldRestrictionRuleRepository(this, fablesDatabase, profiles)
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
