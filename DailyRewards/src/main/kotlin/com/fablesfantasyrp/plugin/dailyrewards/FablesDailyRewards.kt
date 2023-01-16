package com.fablesfantasyrp.plugin.dailyrewards

import com.fablesfantasyrp.plugin.utils.enforceDependencies
import org.bukkit.plugin.java.JavaPlugin


internal val PLUGIN get() = FablesDailyRewards.instance

class FablesDailyRewards : JavaPlugin() {

	override fun onEnable() {
		enforceDependencies(this)
		instance = this

		server.pluginManager.registerEvents(DailyRewardsListener(), this)
	}

	companion object {
		lateinit var instance: FablesDailyRewards
			private set
	}
}
