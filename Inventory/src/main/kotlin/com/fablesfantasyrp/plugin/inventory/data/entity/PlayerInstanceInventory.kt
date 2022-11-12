package com.fablesfantasyrp.plugin.inventory.data.entity

import com.fablesfantasyrp.plugin.database.entity.DataEntity
import com.fablesfantasyrp.plugin.database.repository.DirtyMarker
import com.fablesfantasyrp.plugin.inventory.FablesPlayerInventory
import com.fablesfantasyrp.plugin.inventory.PassthroughPlayerInventory
import com.fablesfantasyrp.plugin.inventory.data.FablesInventoryData

class PlayerInstanceInventory(override val id: Int,
							  val delegate: PassthroughPlayerInventory,
							  override var dirtyMarker: DirtyMarker<PlayerInstanceInventory>? = null)
	: DataEntity<Int, PlayerInstanceInventory>, FablesPlayerInventory by delegate, FablesInventoryData {

	var isDestroyed = false
}
