package com.fablesfantasyrp.plugin.inventory.domain.entity

import com.fablesfantasyrp.plugin.database.entity.DataEntity
import com.fablesfantasyrp.plugin.database.repository.DirtyMarker
import com.fablesfantasyrp.plugin.inventory.PassthroughInventory
import com.fablesfantasyrp.plugin.inventory.PassthroughPlayerInventory

class ProfileInventory(override val id: Int,
					   val inventory: PassthroughPlayerInventory,
					   val enderChest: PassthroughInventory,
					   override var dirtyMarker: DirtyMarker<ProfileInventory>? = null)
	: DataEntity<Int, ProfileInventory> {
	var isDestroyed = false
}
