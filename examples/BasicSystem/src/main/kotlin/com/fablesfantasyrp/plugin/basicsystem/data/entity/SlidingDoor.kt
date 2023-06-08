package com.fablesfantasyrp.plugin.basicsystem.data.entity

import com.fablesfantasyrp.plugin.basicsystem.data.OffsetBlock
import com.fablesfantasyrp.plugin.database.entity.DataEntity
import com.fablesfantasyrp.plugin.database.repository.DirtyMarker
import com.fablesfantasyrp.plugin.utils.BlockCoordinates
import org.bukkit.World

class SlidingDoor : DataEntity<Int, SlidingDoor> {
	override var dirtyMarker: DirtyMarker<SlidingDoor>? = null
	override val id: Int

	var blocks: Collection<OffsetBlock> set(value) { if (field != value) { field = value; dirtyMarker?.markDirty(this) } }
	var handleLocation: BlockCoordinates 	set(value) { if (field != value) { field = value; dirtyMarker?.markDirty(this) } }
	var world: World 					set(value) { if (field != value) { field = value; dirtyMarker?.markDirty(this) } }

	constructor(id: Int,
				blocks: Collection<OffsetBlock>,
				handleLocation: BlockCoordinates,
				world: World,
				dirtyMarker: DirtyMarker<SlidingDoor>? = null) {
		this.id = id
		this.blocks = blocks
		this.handleLocation = handleLocation
		this.world = world

		this.dirtyMarker = dirtyMarker // Must be last
	}
}
