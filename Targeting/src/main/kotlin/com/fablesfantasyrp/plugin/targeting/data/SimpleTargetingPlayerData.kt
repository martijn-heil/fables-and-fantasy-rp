package com.fablesfantasyrp.plugin.targeting.data

import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player

data class SimpleTargetingPlayerData(override val id: OfflinePlayer,
									 override val targets: Collection<Player>,
									 override val isSelecting: Boolean) : TargetingPlayerData
