package com.fablesfantasyrp.plugin.targeting.data

import com.fablesfantasyrp.plugin.database.repository.Identifiable
import org.bukkit.OfflinePlayer

interface TargetingPlayerData : Identifiable<OfflinePlayer> {
	val targets: Collection<OfflinePlayer>
	val isSelecting: Boolean
}
