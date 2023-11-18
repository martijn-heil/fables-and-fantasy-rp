package com.fablesfantasyrp.plugin.lodestones.domain.entity

import com.fablesfantasyrp.plugin.database.entity.DataEntity
import com.fablesfantasyrp.plugin.database.repository.DirtyMarker
import com.fablesfantasyrp.plugin.worldguardinterop.WorldGuardRegion
import org.bukkit.Location

class MapBox : DataEntity<Int, MapBox> {
	override var dirtyMarker: DirtyMarker<MapBox>? = null
	override val id: Int
	val region: WorldGuardRegion
	val location: Location

	constructor(id: Int,
				region: WorldGuardRegion,
				location: Location) {
		this.id = id
		this.region = region
		this.location = location
	}
}
