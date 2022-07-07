package com.fablesfantasyrp.plugin.playerdata

import com.fablesfantasyrp.plugin.playerdata.data.PlayerData
import org.bukkit.entity.Player

class FablesPlayer(rawData: PlayerData) : FablesOfflinePlayer(rawData) {
	override val player: Player
		get() = super.player!!

	companion object {
		fun forPlayer(p: Player) = FablesPlayer(playerDataRepository.forOfflinePlayer(p))
		fun allOnline() = playerDataRepository.allOnline().asSequence().map { FablesPlayer(it) }
	}
}
