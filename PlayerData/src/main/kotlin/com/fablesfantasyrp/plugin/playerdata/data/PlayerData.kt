package com.fablesfantasyrp.plugin.playerdata.data

import net.kyori.adventure.text.format.Style
import org.bukkit.OfflinePlayer
import java.time.Instant

interface PlayerData {
	val offlinePlayer: OfflinePlayer
	var currentCharacterId: ULong?
	var chatChannel: String
	var chatStyle: Style?
	var chatDisabledChannels: Set<String>
	var isTyping: Boolean
	var lastTimeTyping: Instant
	var lastTypingAnimation: String?
}
