package com.fablesfantasyrp.plugin.lodestones.domain.entity

import com.fablesfantasyrp.plugin.database.entity.DataEntity
import com.fablesfantasyrp.plugin.database.repository.DirtyMarker
import com.fablesfantasyrp.plugin.utils.extensions.bukkit.BlockIdentifier

class LodestoneBanner : DataEntity<Int, LodestoneBanner> {
	override var dirtyMarker: DirtyMarker<LodestoneBanner>? = null
	override val id: Int

	var isDestroyed: Boolean = false

	var location: BlockIdentifier	set(value) { if (field != value) { val oldValue = field; field = value; dirtyMarker?.markDirty(this, "location", oldValue, value) } }
	val lodestone: Lodestone

	constructor(id: Int,
				location: BlockIdentifier,
				lodestone: Lodestone,
				dirtyMarker: DirtyMarker<LodestoneBanner>? = null) {
		this.id = id
		this.location = location
		this.lodestone = lodestone

		this.dirtyMarker = dirtyMarker // Must be last
	}
}
