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
package com.fablesfantasyrp.plugin.activity

import com.fablesfantasyrp.plugin.activity.appstart.CommandConfig
import com.fablesfantasyrp.plugin.activity.appstart.KoinConfig
import com.fablesfantasyrp.plugin.activity.domain.repository.ActivityRegionRepository
import com.fablesfantasyrp.plugin.utils.GLOBAL_SYSPREFIX
import com.fablesfantasyrp.plugin.utils.enforceDependencies
import com.github.shynixn.mccoroutine.bukkit.launch
import org.bukkit.plugin.java.JavaPlugin
import org.koin.core.component.KoinComponent
import org.koin.core.component.get


internal val SYSPREFIX = GLOBAL_SYSPREFIX
internal val PLUGIN get() = FablesActivity.instance

class FablesActivity : JavaPlugin(), KoinComponent {
	private lateinit var koinConfig: KoinConfig

	override fun onEnable() {
		enforceDependencies(this)
		instance = this
		saveDefaultConfig()

		koinConfig = KoinConfig(this)
		koinConfig.load()

		launch { warnNotFoundRegions() }

		get<CommandConfig>().init()
	}

	override fun onDisable() {
		get<CommandConfig>().cleanup()
		koinConfig.unload()
	}

	private suspend fun warnNotFoundRegions() {
		get<ActivityRegionRepository>().all().filter { it.region == null }.forEach {
			logger.warning("Could not find WorldGuard region for activity region '${it.id}'")
		}
	}

	companion object {
		lateinit var instance: FablesActivity
			private set
	}
}
