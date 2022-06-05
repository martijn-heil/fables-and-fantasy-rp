package com.fablesfantasyrp.plugin.utilsoffline

import net.flawe.offlinemanager.api.OfflineManagerAPI
import org.bukkit.plugin.java.JavaPlugin

lateinit var offlineManagerAPI: OfflineManagerAPI
	private set

class FablesUtilsOffline : JavaPlugin() {

	override fun onEnable() {
		val plugin = server.pluginManager.getPlugin("OfflineManager")
		if (plugin == null) {
			logger.severe("Could not find OfflineManager! This plugin is required!")
			isEnabled = false
			return
		}
		offlineManagerAPI = plugin as OfflineManagerAPI
	}
}
