package com.fablesfantasyrp.plugin.location.data.entity

import com.fablesfantasyrp.plugin.database.entity.DataEntity
import com.fablesfantasyrp.plugin.database.repository.DirtyMarker
import com.fablesfantasyrp.plugin.location.data.PlayerInstanceLocationData
import com.fablesfantasyrp.plugin.utils.essentialsSpawn
import org.bukkit.Location
import org.bukkit.entity.Player

class PlayerInstanceLocation : DataEntity<Int, PlayerInstanceLocation>, PlayerInstanceLocationData {

	var isDestroyed = false
	override val id: Int
	override var dirtyMarker: DirtyMarker<PlayerInstanceLocation>? = null

	var player: Player? = null
		set(newValue) {
			val oldValue = field
			if (newValue == oldValue) return
			field = null
			if (oldValue != null) location = oldValue.location
			if (newValue != null) {
				if (location.isWorldLoaded) {
					newValue.teleport(location)
				} else {
					newValue.teleport(essentialsSpawn.getSpawn("default").toCenterLocation())
				}
			}
			field = newValue
		}

	override var location: Location
		get() {
			return if (player != null) player!!.location else field
		}
		set(value) {
			if (player != null) {
				player!!.teleport(value)
			} else {
				field = value
			}
		}

	constructor(id: Int, location: Location, dirtyMarker: DirtyMarker<PlayerInstanceLocation>? = null) {
		this.id = id
		this.location = location
		this.dirtyMarker = dirtyMarker
	}
}
