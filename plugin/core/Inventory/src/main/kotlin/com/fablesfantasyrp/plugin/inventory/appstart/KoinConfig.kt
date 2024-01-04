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
			ProfileInventoryRepositoryImpl(mapper, get())
		} bind ProfileInventoryRepository::class

		singleOf(::MirroredInventoryManager)
		singleOf(::ProfileInventoryListener)
	}
}
