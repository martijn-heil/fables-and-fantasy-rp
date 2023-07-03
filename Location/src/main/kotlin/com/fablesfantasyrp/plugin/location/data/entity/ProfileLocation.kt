package com.fablesfantasyrp.plugin.location.data.entity

import com.fablesfantasyrp.plugin.database.entity.DataEntity
import com.fablesfantasyrp.plugin.database.repository.DirtyMarker
import com.fablesfantasyrp.plugin.location.data.ProfileLocationData
import com.fablesfantasyrp.plugin.utils.SPAWN
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
				if(!oldValue.teleport(SPAWN)) throw IllegalStateException("Teleport of previous occupant failed")
			}
			if (newValue != null) {
				if(!newValue.teleport(location)) throw IllegalStateException("Teleport failed")
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
