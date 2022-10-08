package com.fablesfantasyrp.plugin.locks.data

import org.bukkit.Location
import org.bukkit.OfflinePlayer

data class SimpleLockData(override val owner: OfflinePlayer,
						  override val members: Map<OfflinePlayer, LockRole>,
						  override val id: Location) : LockData { companion object }
