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
package com.fablesfantasyrp.plugin.characters

import com.fablesfantasyrp.plugin.characters.appstart.CommandConfig
import com.fablesfantasyrp.plugin.characters.appstart.KoinConfig
import com.fablesfantasyrp.plugin.characters.domain.repository.CharacterRepository
import com.fablesfantasyrp.plugin.characters.domain.repository.CharacterRepositoryImpl
import com.fablesfantasyrp.plugin.characters.nametags.NameTagManager
import com.fablesfantasyrp.plugin.characters.web.WebHook
import com.fablesfantasyrp.plugin.database.applyMigrations
import com.fablesfantasyrp.plugin.database.scheduleRepeatingDataSave
import com.fablesfantasyrp.plugin.utils.GLOBAL_SYSPREFIX
import com.fablesfantasyrp.plugin.utils.enforceDependencies
import org.bukkit.plugin.ServicePriority
import org.bukkit.plugin.java.JavaPlugin
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

internal val SYSPREFIX = GLOBAL_SYSPREFIX

internal val PLUGIN get() = FablesCharacters.instance

class FablesCharacters : JavaPlugin(), KoinComponent {
	private lateinit var koinConfig: KoinConfig

	override fun onEnable() {
		enforceDependencies(this)
		instance = this

		try {
			applyMigrations(this, "FABLES_CHARACTERS", this.classLoader)
		} catch (e: Exception) {
			this.isEnabled = false
			return
		}

		koinConfig = KoinConfig(this)
		koinConfig.load()

		(get<CharacterRepository>() as CharacterRepositoryImpl).init()

		server.servicesManager.register(CharacterRepository::class.java, get(), this, ServicePriority.Normal)

		get<CommandConfig>().init()

		server.pluginManager.registerEvents(get<CharactersListener>(), this)
		server.pluginManager.registerEvents(get<CharactersLiveMigrationListener>(), this)
		server.pluginManager.registerEvents(get<CharacterCreationListener>(), this)

		if (server.pluginManager.isPluginEnabled("TAB") && server.pluginManager.isPluginEnabled("Denizen") ) {
			logger.info("Enabling TAB integration")
			NameTagManager(get(), get()).start()
		}

		if (server.pluginManager.isPluginEnabled("FablesWeb")) {
			try {
				logger.info("Enabling FablesWeb integration")
				WebHook().start()
			} catch (ex: Exception) {
				ex.printStackTrace()
				logger.warning("An error occurred during setup of FablesWeb integration.")
			}
		}

		/*server.scheduler.scheduleSyncRepeatingTask(this, {
			logger.info("Saving characters..")
			get<CharacterRepositoryImpl>().saveAllDirty()
		}, 0, 6000)

		server.scheduler.scheduleSyncRepeatingTask(this, {
			logger.info("Saving character traits..")
			get<CharacterTraitRepositoryImpl>().saveAllDirty()
		}, 0, 6000)*/

		scheduleRepeatingDataSave(this) { get<CharacterRepositoryImpl>().saveAllDirty() }
	}

	override fun onDisable() {
		get<CommandConfig>().cleanup()
		frunBlocking { get<CharacterRepositoryImpl>().saveAllDirty() }
		koinConfig.unload()
	}

	companion object {
		lateinit var instance: FablesCharacters
	}
}
