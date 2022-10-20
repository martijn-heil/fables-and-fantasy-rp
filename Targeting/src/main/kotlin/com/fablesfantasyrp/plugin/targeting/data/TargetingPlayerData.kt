package com.fablesfantasyrp.plugin.targeting.data

import com.fablesfantasyrp.plugin.database.repository.Identifiable
import org.bukkit.OfflinePlayer

interface TargetingPlayerData : Identifiable<OfflinePlayer> {
	val targets: Set<OfflinePlayer>
	val isSelecting: Boolean
}
