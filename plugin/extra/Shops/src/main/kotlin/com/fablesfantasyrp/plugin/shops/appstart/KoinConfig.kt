package com.fablesfantasyrp.plugin.shops.appstart

import com.fablesfantasyrp.plugin.shops.*
import com.fablesfantasyrp.plugin.shops.command.ShopCommand
import com.fablesfantasyrp.plugin.shops.command.provider.ShopModule
import com.fablesfantasyrp.plugin.shops.dal.h2.H2ShopDataRepository
import com.fablesfantasyrp.plugin.shops.domain.mapper.ShopMapper
import com.fablesfantasyrp.plugin.shops.domain.repository.ShopRepository
import com.fablesfantasyrp.plugin.shops.domain.repository.ShopRepositoryImpl
import com.fablesfantasyrp.plugin.utils.koin.BaseKoinConfig
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
			val h2Repository = H2ShopDataRepository(get())
			val mapper = ShopMapper(h2Repository, get())
			val domainRepository = ShopRepositoryImpl(mapper)
			domainRepository
		} bind ShopRepository::class

		singleOf(::CommandConfig)
		singleOf(::ShopSlotCountCalculatorImpl) bind ShopSlotCountCalculator::class

		factoryOf(::ShopModule)
		factoryOf(::ShopCommand)
		factoryOf(::ShopAuthorizerImpl) bind ShopAuthorizer::class
	}
}
