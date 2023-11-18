package com.fablesfantasyrp.plugin.discord.web

import com.fablesfantasyrp.plugin.web.loaders.WebRoutingLoader
import org.koin.core.context.loadKoinModules
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

class WebHook {
	private lateinit var koinModule: Module

	fun start() {
		koinModule = module(createdAtStart = true) {
			singleOf(::WebRouting) bind WebRoutingLoader::class
		}
		loadKoinModules(koinModule)
	}
}
