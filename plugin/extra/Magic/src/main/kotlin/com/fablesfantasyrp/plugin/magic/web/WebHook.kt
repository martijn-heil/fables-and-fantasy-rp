package com.fablesfantasyrp.plugin.magic.web

import com.fablesfantasyrp.plugin.web.loaders.WebRequestValidationLoader
import com.fablesfantasyrp.plugin.web.loaders.WebRoutingLoader
import org.koin.core.context.loadKoinModules
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

internal class WebHook {
	private lateinit var koinModule: Module

	fun start() {
		koinModule = module(createdAtStart = true) {
			singleOf(::WebRouting) bind WebRoutingLoader::class
			singleOf(::WebRequestValidation) bind WebRequestValidationLoader::class
		}
		loadKoinModules(koinModule)
	}
}
