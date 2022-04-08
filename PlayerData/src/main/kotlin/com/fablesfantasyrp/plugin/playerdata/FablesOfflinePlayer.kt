package com.fablesfantasyrp.plugin.playerdata

import com.fablesfantasyrp.plugin.playerdata.database.DatabasePlayerData
import com.fablesfantasyrp.plugin.playerdata.database.forOfflinePlayer
import org.bukkit.OfflinePlayer

class FablesOfflinePlayer(val rawData: PlayerData) {
	val offlinePlayer = rawData.offlinePlayer
	val player = offlinePlayer.player

	companion object {
		fun forOfflinePlayer(p: OfflinePlayer) = FablesOfflinePlayer(DatabasePlayerData.forOfflinePlayer(p))
	}
}

val OfflinePlayer.fablesOfflinePlayer
	get() = FablesOfflinePlayer.forOfflinePlayer(this)
