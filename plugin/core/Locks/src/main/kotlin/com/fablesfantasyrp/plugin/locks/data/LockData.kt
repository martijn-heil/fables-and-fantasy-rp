package com.fablesfantasyrp.plugin.locks.data

import com.fablesfantasyrp.plugin.database.repository.Identifiable
import org.bukkit.GameMode
import org.bukkit.Location
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player

interface LockData : Identifiable<Location> {
	val owner: OfflinePlayer
	val members: Map<OfflinePlayer, LockRole>
	val location get() = id

	fun calculateAccess(p: Player): LockAccess {
		return when {
			(p.hasPermission("locks.bypass") && p.gameMode == GameMode.CREATIVE) -> LockAccess.STAFF
			this.owner.uniqueId == p.uniqueId -> LockAccess.OWNER
			else -> members[p]?.let { LockAccess.fromRole(it) } ?: LockAccess.NONE
		}
	}
}
