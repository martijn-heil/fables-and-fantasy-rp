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
package com.fablesfantasyrp.plugin.location

import com.fablesfantasyrp.plugin.database.applyMigrations
import com.fablesfantasyrp.plugin.domain.SPAWN
import com.fablesfantasyrp.plugin.location.data.entity.EntityProfileLocationRepository
import com.fablesfantasyrp.plugin.location.data.entity.ProfileLocationRepository
import com.fablesfantasyrp.plugin.location.data.persistent.H2ProfileLocationRepository
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

internal val PLUGIN get() = FablesLocation.instance

class FablesLocation : JavaPlugin(), KoinComponent {
	private lateinit var koinModule: Module
	private lateinit var profileManager: ProfileManager
	private lateinit var profileLocationRepository: EntityProfileLocationRepository<*>

	override fun onEnable() {
		enforceDependencies(this)
		instance = this


		try {
			applyMigrations(this, "FABLES_LOCATION", this.classLoader)
		} catch (e: Exception) {
			this.isEnabled = false
			return
		}

		koinModule = module(createdAtStart = true) {
			single<Plugin> { this@FablesLocation } binds(arrayOf(JavaPlugin::class))

			single {
				val tmp = EntityProfileLocationRepository(H2ProfileLocationRepository(get(), get(), get(), SPAWN))
				tmp.init()
				tmp
			} bind ProfileLocationRepository::class

			singleOf(::ProfileLocationListener)
		}
		loadKoinModules(koinModule)

		profileManager = get()
		profileLocationRepository = get()

		server.pluginManager.registerEvents(get<ProfileLocationListener>(), this)

		/*server.scheduler.scheduleSyncRepeatingTask(this, {
			logger.info("Saving locations..")
			profileLocationRepository.saveAll()
		}, 0, 6000)*/
	}

	override fun onDisable() {
		server.onlinePlayers.forEach {
			val profile = profileManager.getCurrentForPlayer(it) ?: return@forEach
			profileLocationRepository.forOwner(profile).player = null
		}
		profileLocationRepository.saveAll()
		unloadKoinModules(koinModule)
	}

	companion object {
		lateinit var instance: FablesLocation
	}
}
