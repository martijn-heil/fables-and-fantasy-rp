package com.fablesfantasyrp.plugin.playerdata

import com.fablesfantasyrp.plugin.playerdata.database.DatabasePlayerData
import com.fablesfantasyrp.plugin.playerdata.database.forOfflinePlayer
import org.bukkit.entity.Player

class FablesPlayer(rawData: PlayerData) : FablesOfflinePlayer(rawData) {
	override val player: Player = super.player!!

	companion object {
		fun forPlayer(p: Player) = FablesPlayer(DatabasePlayerData.forOfflinePlayer(p))
	}
}
