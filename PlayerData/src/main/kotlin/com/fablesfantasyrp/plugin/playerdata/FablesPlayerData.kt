package com.fablesfantasyrp.plugin.playerdata

import com.fablesfantasyrp.plugin.playerdata.database.DatabasePlayerRepository
import org.bukkit.plugin.java.JavaPlugin

lateinit var playerDataRepository: DatabasePlayerRepository
	private set

class FablesPlayerData : JavaPlugin() {
	override fun onEnable() {
		instance = this
		playerDataRepository = DatabasePlayerRepository(this)
		//server.offlinePlayers.forEach { ensurePresenceInDatabase(it) }
	}

	override fun onDisable() {
		playerDataRepository.saveAllDirty()
	}

	companion object {
		lateinit var instance: FablesPlayerData
	}
}
