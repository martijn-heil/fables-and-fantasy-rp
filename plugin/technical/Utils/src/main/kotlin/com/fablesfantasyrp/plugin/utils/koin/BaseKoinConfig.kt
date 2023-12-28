package com.fablesfantasyrp.plugin.utils.koin

import org.koin.core.context.loadKoinModules
import org.koin.core.context.unloadKoinModules
import org.koin.core.module.Module

abstract class BaseKoinConfig {
	abstract val koinModule: Module

	open fun load() {
		loadKoinModules(koinModule)
	}

	open fun unload() {
		unloadKoinModules(koinModule)
	}
}
