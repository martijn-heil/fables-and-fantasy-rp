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
package com.fablesfantasyrp.plugin.location.data.entity

import com.fablesfantasyrp.plugin.database.entity.DataEntity
import com.fablesfantasyrp.plugin.database.repository.DirtyMarker
import com.fablesfantasyrp.plugin.domain.SPAWN
import com.fablesfantasyrp.plugin.location.data.ProfileLocationData
import org.bukkit.Location
import org.bukkit.entity.Player

class ProfileLocation : DataEntity<Int, ProfileLocation>, ProfileLocationData {

	var isDestroyed = false
	override val id: Int
	override var dirtyMarker: DirtyMarker<ProfileLocation>? = null

	var player: Player? = null
		set(newValue) {
			//val newValue = newValueTmp?.uniqueId?.let { Bukkit.getPlayer(it) }
			val oldValue = field
			if (newValue == oldValue) return
			field = null
			if (oldValue != null) {
				location = oldValue.location
				if(!oldValue.teleport(SPAWN) && oldValue.isConnected) throw IllegalStateException("Teleport of previous occupant failed")
			}
			if (newValue != null) {
				if(!newValue.teleport(location) && newValue.isConnected) throw IllegalStateException("Teleport failed")
			}
			field = newValue
		}

	override var location: Location
		get() {
			return if (player != null) player!!.location else field
		}
		set(value) {
			if (value != field) dirtyMarker?.markDirty(this)
			if (player != null) {
				player!!.teleport(value)
			} else {
				field = value
			}
		}

	constructor(id: Int, location: Location, dirtyMarker: DirtyMarker<ProfileLocation>? = null) {
		this.id = id
		this.location = location
		this.dirtyMarker = dirtyMarker
	}
}
