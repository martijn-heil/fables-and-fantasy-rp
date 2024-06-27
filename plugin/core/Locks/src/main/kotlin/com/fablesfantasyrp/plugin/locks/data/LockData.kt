/*
 * Fables and Fantasy RP kotlin plugins.
 * Copyright (C) 2024  Martijn Heil
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.fablesfantasyrp.plugin.locks.data

import com.fablesfantasyrp.plugin.database.model.Identifiable
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
