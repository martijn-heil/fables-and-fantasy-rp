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
