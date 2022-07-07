package com.fablesfantasyrp.plugin.playerdata

import com.fablesfantasyrp.plugin.playerdata.data.PlayerData
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer

open class FablesOfflinePlayer(val rawData: PlayerData)  {
	val offlinePlayer = rawData.offlinePlayer

	// For some reason offlinePlayer.player is not reliable, so we look the player up based on UUID instead
	open val player
		get() = Bukkit.getPlayer(offlinePlayer.uniqueId)

	companion object {
		fun forOfflinePlayer(p: OfflinePlayer) = FablesOfflinePlayer(playerDataRepository.forOfflinePlayer(p))
	}

	override fun equals(other: Any?) = other is FablesOfflinePlayer && other.rawData == this.rawData
	override fun hashCode(): Int = rawData.hashCode()
}
