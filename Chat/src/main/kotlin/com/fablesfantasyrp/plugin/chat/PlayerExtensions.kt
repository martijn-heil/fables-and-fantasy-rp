package com.fablesfantasyrp.plugin.chat

import com.fablesfantasyrp.plugin.chat.data.ChatPlayerData
import com.fablesfantasyrp.plugin.chat.data.entity.ChatPlayerEntity
import org.bukkit.OfflinePlayer

val OfflinePlayer.chat: ChatPlayerEntity
	get() = chatPlayerDataManager.forId(uniqueId)!!
