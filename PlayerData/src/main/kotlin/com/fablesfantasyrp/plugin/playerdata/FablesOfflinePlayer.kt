package com.fablesfantasyrp.plugin.playerdata

import com.fablesfantasyrp.plugin.playerdata.database.DatabasePlayerData
import com.fablesfantasyrp.plugin.playerdata.database.forOfflinePlayer
import org.bukkit.OfflinePlayer

open class FablesOfflinePlayer(val rawData: PlayerData)  {
	val offlinePlayer = rawData.offlinePlayer
	open val player = offlinePlayer.player

	companion object {
		fun forOfflinePlayer(p: OfflinePlayer) = FablesOfflinePlayer(DatabasePlayerData.forOfflinePlayer(p))
	}

	override fun equals(other: Any?) = other is FablesOfflinePlayer && other.rawData == this.rawData
	override fun hashCode(): Int = rawData.hashCode()
}

val OfflinePlayer.fablesOfflinePlayer
	get() = FablesOfflinePlayer.forOfflinePlayer(this)
