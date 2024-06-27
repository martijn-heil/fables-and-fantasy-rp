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
package com.fablesfantasyrp.plugin.morelogging

import com.fablesfantasyrp.plugin.utils.enforceDependencies
import org.bukkit.plugin.java.JavaPlugin
import org.koin.core.component.KoinComponent
import org.koin.core.context.loadKoinModules
import org.koin.core.context.unloadKoinModules
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module


class FablesMoreLogging : JavaPlugin(), KoinComponent {
	private lateinit var koinModule: Module

	override fun onEnable() {
		enforceDependencies(this)
		instance = this

		koinModule = module(createdAtStart = true) {
			singleOf(::StaffActionBroadcasterImpl) bind StaffActionBroadcaster::class
		}
		loadKoinModules(koinModule)

		ModerationLoggerManager(this).start()

		MODERATION_LOGGER.info("Logging system starting up")

		server.pluginManager.registerEvents(BukkitListener(MODERATION_LOGGER, this), this)

		if (server.pluginManager.isPluginEnabled("SuperVanish")) {
			server.pluginManager.registerEvents(SuperVanishListener(MODERATION_LOGGER, this), this)
		}
	}

	override fun onDisable() {
		MODERATION_LOGGER.info("Logging system shutting down")
		unloadKoinModules(koinModule)
	}

	companion object {
		lateinit var instance: FablesMoreLogging
	}
}
