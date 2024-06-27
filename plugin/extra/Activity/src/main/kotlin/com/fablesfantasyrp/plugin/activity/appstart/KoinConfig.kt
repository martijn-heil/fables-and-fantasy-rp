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
package com.fablesfantasyrp.plugin.activity.appstart

import com.fablesfantasyrp.plugin.activity.command.Commands
import com.fablesfantasyrp.plugin.activity.dal.repository.ActivityRegionDataRepository
import com.fablesfantasyrp.plugin.activity.dal.yaml.YamlActivityRegionDataRepository
import com.fablesfantasyrp.plugin.activity.domain.mapper.ActivityRegionMapper
import com.fablesfantasyrp.plugin.activity.domain.repository.ActivityRegionRepository
import com.fablesfantasyrp.plugin.activity.domain.repository.ActivityRegionRepositoryImpl
import com.fablesfantasyrp.plugin.utils.koin.BaseKoinConfig
import org.bukkit.plugin.Plugin
import org.bukkit.plugin.java.JavaPlugin
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.binds
import org.koin.dsl.module

internal class KoinConfig(private val plugin: Plugin) : BaseKoinConfig() {
	override val koinModule = module(true) {
		single <Plugin> { plugin } binds(arrayOf(JavaPlugin::class))

		single<ActivityRegionDataRepository> { YamlActivityRegionDataRepository(plugin.config) }
		singleOf(::ActivityRegionMapper)
		singleOf(::ActivityRegionRepositoryImpl) bind ActivityRegionRepository::class

		singleOf(::Commands)
		singleOf(::CommandConfig)
	}
}
