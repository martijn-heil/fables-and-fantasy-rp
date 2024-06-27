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
package com.fablesfantasyrp.plugin.hacks

import net.milkbowl.vault.chat.Chat
import net.milkbowl.vault.permission.Permission
import org.bukkit.plugin.Plugin
import org.bukkit.plugin.ServicePriority
import org.bukkit.plugin.java.JavaPlugin
import org.koin.core.component.KoinComponent
import org.koin.core.context.loadKoinModules
import org.koin.core.context.unloadKoinModules
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.binds
import org.koin.dsl.module
import java.util.*

internal val NINJOH_NAME = "Ninjoh"
internal val NINJOH_UUID = UUID.fromString("50d8fcf0-166e-4ab3-9176-c41fb575071a")

class FablesHacks : JavaPlugin(), KoinComponent {
	private lateinit var koinModule: Module

	override fun onEnable() {
		val perms = server.servicesManager.getRegistration(Permission::class.java)!!
		val chat = server.servicesManager.getRegistration(Chat::class.java)!!

		val permissionInjector = PermissionInjectorImpl(this, perms.provider)

		server.servicesManager.register(Permission::class.java,
			permissionInjector, this, ServicePriority.Highest)

		server.servicesManager.register(Chat::class.java,
			HackyVaultChat(chat.provider, permissionInjector), this, ServicePriority.Highest)

		koinModule = module(createdAtStart = false) {
			single<Plugin> { this@FablesHacks } binds(arrayOf(JavaPlugin::class))

			//singleOf(::FlippedPlayerManager)
			singleOf(::HackyListener)
			single<PermissionInjector> { permissionInjector }
		}
		loadKoinModules(koinModule)

		/*server.scheduler.scheduleSyncDelayedTask(this, {
			get<FlippedPlayerManager>().start()
			server.pluginManager.registerEvents(get<HackyListener>(), this)
		}, 1)*/
	}

	override fun onDisable() {
		//get<FlippedPlayerManager>().stop()
		unloadKoinModules(koinModule)
	}
}
