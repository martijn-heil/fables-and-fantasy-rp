package com.fablesfantasyrp.plugin.fasttravel.data.entity

import com.fablesfantasyrp.plugin.database.entity.DataEntity
import com.fablesfantasyrp.plugin.database.repository.DirtyMarker
import com.fablesfantasyrp.plugin.fasttravel.data.FastTravelLinkData
import com.fablesfantasyrp.plugin.worldguardinterop.WorldGuardRegion
import org.bukkit.Location
import kotlin.time.Duration

class FastTravelLink : DataEntity<Int, FastTravelLink>, FastTravelLinkData {
	override val from: WorldGuardRegion
	override val to: Location
	override val travelDuration: Duration
	override val id: Int
	override var dirtyMarker: DirtyMarker<FastTravelLink>? = null
	var isDestroyed = false

	constructor(id: Int, from: WorldGuardRegion, to: Location, travelDuration: Duration,
				dirtyMarker: DirtyMarker<FastTravelLink>? = null) {
		this.id = id
		this.dirtyMarker = dirtyMarker
		this.from = from
		this.to = to
		this.travelDuration = travelDuration
	}
}
