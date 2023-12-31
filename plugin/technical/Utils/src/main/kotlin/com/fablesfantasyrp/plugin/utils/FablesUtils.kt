package com.fablesfantasyrp.plugin.utils

import com.fablesfantasyrp.plugin.utils.domain.premium.PremiumRankCalculator
import com.fablesfantasyrp.plugin.utils.domain.premium.PremiumRankCalculatorImpl
import org.bukkit.Bukkit
import org.bukkit.Server
import org.bukkit.plugin.java.JavaPlugin
import org.koin.core.context.GlobalContext.startKoin
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module


class FablesUtils : JavaPlugin() {

	override fun onEnable() {
		Services.register(Server::class, server, this)

		val commonKoinModule = module {
			single { Bukkit.getServer() }
			singleOf (::PremiumRankCalculatorImpl) bind PremiumRankCalculator::class
		}

		startKoin {
			modules(commonKoinModule)
		}
	}
}
