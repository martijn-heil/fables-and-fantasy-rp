package com.fablesfantasyrp.plugin.lodestones.domain.entity

import com.fablesfantasyrp.plugin.database.entity.DataEntity
import com.fablesfantasyrp.plugin.database.repository.DirtyMarker
import com.fablesfantasyrp.plugin.database.repository.Named
import com.fablesfantasyrp.plugin.utils.extensions.bukkit.BlockIdentifier
import org.bukkit.entity.Player

class Lodestone : DataEntity<Int, Lodestone>, Named {
	override var dirtyMarker: DirtyMarker<Lodestone>? = null
	override val id: Int

	var isDestroyed: Boolean = false

	var location: BlockIdentifier	set(value) { if (field != value) { val oldValue = field; field = value; dirtyMarker?.markDirty(this, "location", oldValue, value) } }
	override var name: String 		set(value) { if (field != value) { val oldValue = field; field = value; dirtyMarker?.markDirty(this, "name", oldValue, value) } }

	fun warpHere(player: Player) {
		val loc = location.toLocation().toCenterLocation()
		loc.y += 1
		player.teleport(loc)
	}

	constructor(id: Int,
				location: BlockIdentifier,
				name: String,
				dirtyMarker: DirtyMarker<Lodestone>? = null) {
		this.id = id
		this.location = location
		this.name = name

		this.dirtyMarker = dirtyMarker // Must be last
	}
}
