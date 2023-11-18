package com.fablesfantasyrp.plugin.utilsoffline

import com.fablesfantasyrp.plugin.utils.enforceDependencies
import net.quazar.offlinemanager.api.OfflineManagerAPI
import org.bukkit.plugin.java.JavaPlugin

lateinit var offlineManagerAPI: OfflineManagerAPI
	private set

class FablesUtilsOffline : JavaPlugin() {

	override fun onEnable() {
		enforceDependencies(this)
		offlineManagerAPI = server.pluginManager.getPlugin("OfflineManager") as OfflineManagerAPI
	}
}
