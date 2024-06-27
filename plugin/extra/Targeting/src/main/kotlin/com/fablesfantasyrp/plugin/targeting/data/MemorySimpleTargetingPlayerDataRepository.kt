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
package com.fablesfantasyrp.plugin.targeting.data

import com.fablesfantasyrp.plugin.database.repository.SimpleMapRepository
import com.fablesfantasyrp.plugin.glowing.GlowingManager
import com.fablesfantasyrp.plugin.targeting.Permission
import org.bukkit.OfflinePlayer

class MemorySimpleTargetingPlayerDataRepository(private val glowingManager: GlowingManager) :
		SimpleMapRepository<OfflinePlayer, SimpleTargetingPlayerData>(), SimpleTargetingPlayerDataRepository {
	override fun update(v: SimpleTargetingPlayerData) {
		val current = this.forId(v.id)!!
		super.update(v)

		val offlinePlayer = v.id
		val player = offlinePlayer.player
		if (offlinePlayer.isOnline && player != null) {
			val removed = current.targets.minus(v.targets.toSet())
			val added = v.targets.minus(current.targets.toSet())

			if (player.hasPermission(Permission.Glowingvisuals)) {
				removed.forEach { glowingManager.setIsGlowingFor(it, player, false) }
				added.forEach { glowingManager.setIsGlowingFor(it, player, true) }
			}
		}
	}

	override fun forId(id: OfflinePlayer): SimpleTargetingPlayerData? {
		return super.forId(id) ?: SimpleTargetingPlayerData(id, emptySet(), false)
	}
}
