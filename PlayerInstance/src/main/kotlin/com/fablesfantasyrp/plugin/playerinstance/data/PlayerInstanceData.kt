package com.fablesfantasyrp.plugin.playerinstance.data

import com.fablesfantasyrp.plugin.database.repository.Identifiable
import org.bukkit.OfflinePlayer

interface PlayerInstanceData : Identifiable<Int> {
	val owner: OfflinePlayer
}
