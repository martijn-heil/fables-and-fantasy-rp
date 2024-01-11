package com.fablesfantasyrp.plugin.domain

import com.fablesfantasyrp.plugin.domain.premium.PremiumRankCalculator
import com.fablesfantasyrp.plugin.domain.premium.PremiumRankCalculatorImpl
import com.fablesfantasyrp.plugin.utils.enforceDependencies
import org.bukkit.plugin.java.JavaPlugin
import org.koin.core.component.KoinComponent
import org.koin.core.context.loadKoinModules
import org.koin.core.context.unloadKoinModules
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module


internal val PLUGIN get() = FablesDomain.instance

class FablesDomain : JavaPlugin(), KoinComponent {
	private lateinit var koinModule: Module

	override fun onEnable() {
		enforceDependencies(this)
		instance = this

		koinModule = module(true) {
			singleOf (::PremiumRankCalculatorImpl) bind PremiumRankCalculator::class
		}
		loadKoinModules(koinModule)
	}

	override fun onDisable() {
		unloadKoinModules(koinModule)
	}

	companion object {
		lateinit var instance: FablesDomain
			private set
	}
}
