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
package com.fablesfantasyrp.plugin.shops.appstart

import com.fablesfantasyrp.caturix.spigot.common.bukkit.provider.sender.BukkitSenderProvider
import com.fablesfantasyrp.plugin.shops.*
import com.fablesfantasyrp.plugin.shops.command.ShopCommand
import com.fablesfantasyrp.plugin.shops.command.provider.ShopModule
import com.fablesfantasyrp.plugin.shops.dal.h2.H2ShopDataRepository
import com.fablesfantasyrp.plugin.shops.domain.mapper.ShopMapper
import com.fablesfantasyrp.plugin.shops.domain.repository.ShopRepository
import com.fablesfantasyrp.plugin.shops.domain.repository.ShopRepositoryImpl
import com.fablesfantasyrp.plugin.shops.service.DisplayItemService
import com.fablesfantasyrp.plugin.shops.service.DisplayItemServiceImpl
import com.fablesfantasyrp.plugin.utils.koin.BaseKoinConfig
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin
import org.bukkit.plugin.java.JavaPlugin
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.binds
import org.koin.dsl.module

internal class KoinConfig(private val plugin: JavaPlugin) : BaseKoinConfig() {
	override val koinModule: Module = module(true) {
		single<Plugin> { plugin } binds(arrayOf(JavaPlugin::class))

		singleOf(::ShopListener)

		single {
			val h2Repository = H2ShopDataRepository(get(), get())
			val mapper = ShopMapper(h2Repository, get())
			val domainRepository = ShopRepositoryImpl(mapper, get())
			domainRepository
		} bind ShopRepository::class

		singleOf(::CommandConfig)
		singleOf(::ShopSlotCountCalculatorImpl) bind ShopSlotCountCalculator::class
		singleOf(::DisplayItemServiceImpl) bind DisplayItemService::class

		factory { ShopModule(get(), BukkitSenderProvider(Player::class.java)) }
		factoryOf(::ShopCommand)
		factoryOf(::ShopAuthorizerImpl) bind ShopAuthorizer::class
	}
}
