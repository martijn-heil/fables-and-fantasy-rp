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
package com.fablesfantasyrp.plugin.inventory

import com.fablesfantasyrp.plugin.database.applyMigrations
import com.fablesfantasyrp.plugin.database.scheduleRepeatingDataSave
import com.fablesfantasyrp.plugin.inventory.appstart.KoinConfig
import com.fablesfantasyrp.plugin.inventory.domain.repository.ProfileInventoryRepositoryImpl
import com.fablesfantasyrp.plugin.inventory.service.MirroredInventoryManager
import com.fablesfantasyrp.plugin.profile.ProfileManager
import com.fablesfantasyrp.plugin.utils.enforceDependencies
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import org.bukkit.plugin.java.JavaPlugin
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

internal val PLUGIN get() = FablesInventoryPlugin.instance

class FablesInventoryPlugin : JavaPlugin(), KoinComponent {
	private lateinit var koinConfig: KoinConfig

	override fun onEnable() {
		enforceDependencies(this)
		instance = this


		try {
			applyMigrations(this, "FABLES_INVENTORY", this.classLoader)
		} catch (e: Exception) {
			this.isEnabled = false
			return
		}

		koinConfig = KoinConfig(this)
		koinConfig.load()

		get<ProfileInventoryRepositoryImpl>().init()
		get<MirroredInventoryManager>().start()

		server.pluginManager.registerEvents(get<ProfileInventoryListener>(), this)

		scheduleRepeatingDataSave(this) { get<ProfileInventoryRepositoryImpl>().saveAll() }
	}

	override fun onDisable() {
		val repository = get<ProfileInventoryRepositoryImpl>()
		frunBlocking {
			server.onlinePlayers.asFlow().onEach {
				val profile = get<ProfileManager>().getCurrentForPlayer(it) ?: return@onEach
				val profileInventory = repository.forOwner(profile)
				profileInventory.inventory.bukkitInventory = null
				profileInventory.enderChest.bukkitInventory = null
			}.collect()

			get<ProfileInventoryRepositoryImpl>().saveAll()
		}
		get<MirroredInventoryManager>().stop()
		koinConfig.unload()
	}

	companion object {
		lateinit var instance: FablesInventoryPlugin
	}
}
