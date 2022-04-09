package com.fablesfantasyrp.plugin.playerdata

import org.bukkit.OfflinePlayer

interface PlayerData {
	val offlinePlayer: OfflinePlayer
	var currentCharacterId: ULong?
	var chatChannel: String
}
