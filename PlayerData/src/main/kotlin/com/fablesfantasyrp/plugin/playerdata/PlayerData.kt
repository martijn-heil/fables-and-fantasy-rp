package com.fablesfantasyrp.plugin.playerdata

import net.kyori.adventure.text.format.Style
import org.bukkit.OfflinePlayer

interface PlayerData {
	val offlinePlayer: OfflinePlayer
	var currentCharacterId: ULong?
	var chatChannel: String
	var chatStyle: Style?
	var chatDisabledChannels: Set<String>
}
