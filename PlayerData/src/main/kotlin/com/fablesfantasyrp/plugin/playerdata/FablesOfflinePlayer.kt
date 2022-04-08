package com.fablesfantasyrp.plugin.playerdata

import com.fablesfantasyrp.plugin.playerdata.database.DatabasePlayer
import com.fablesfantasyrp.plugin.playerdata.database.forOfflinePlayer
import org.bukkit.OfflinePlayer

class FablesOfflinePlayer(val rawData: DatabasePlayer) {
	val offlinePlayer = rawData.player

	companion object {
		fun forOfflinePlayer(p: OfflinePlayer) = FablesOfflinePlayer(DatabasePlayer.forOfflinePlayer(p))
	}
}

val OfflinePlayer.fablesOfflinePlayer
	get() = FablesOfflinePlayer.forOfflinePlayer(this)
