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
package com.fablesfantasyrp.plugin.inventory.appstart

import com.fablesfantasyrp.plugin.inventory.ProfileInventoryListener
import com.fablesfantasyrp.plugin.inventory.dal.h2.H2ProfileInventoryDataRepository
import com.fablesfantasyrp.plugin.inventory.domain.mapper.ProfileInventoryMapper
import com.fablesfantasyrp.plugin.inventory.domain.repository.ProfileInventoryRepository
import com.fablesfantasyrp.plugin.inventory.domain.repository.ProfileInventoryRepositoryImpl
import com.fablesfantasyrp.plugin.inventory.service.MirroredInventoryManager
import com.fablesfantasyrp.plugin.utils.koin.BaseKoinConfig
import org.bukkit.plugin.Plugin
import org.bukkit.plugin.java.JavaPlugin
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.binds
import org.koin.dsl.module

internal class KoinConfig(private val plugin: Plugin) : BaseKoinConfig() {
	override val koinModule = module(true) {
		single<Plugin> { plugin } binds(arrayOf(JavaPlugin::class))

		single {
			val dalRepository = H2ProfileInventoryDataRepository(get())
			val mapper = ProfileInventoryMapper(dalRepository)
			ProfileInventoryRepositoryImpl(mapper)
		} bind ProfileInventoryRepository::class

		singleOf(::MirroredInventoryManager)
		singleOf(::ProfileInventoryListener)
	}
}
