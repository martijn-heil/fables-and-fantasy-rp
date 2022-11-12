package com.fablesfantasyrp.plugin.inventory.data.entity

import com.fablesfantasyrp.plugin.database.entity.MassivelyCachingEntityRepository
import com.fablesfantasyrp.plugin.database.repository.HasDirtyMarker
import com.fablesfantasyrp.plugin.database.repository.KeyedRepository
import com.fablesfantasyrp.plugin.database.repository.MutableRepository
import com.fablesfantasyrp.plugin.playerinstance.data.entity.PlayerInstance

class EntityFablesInventoryRepository<C>(private val child: C)
	: MassivelyCachingEntityRepository<Int, PlayerInstanceInventory, C>(child), FablesInventoryRepository
		where C: KeyedRepository<Int, PlayerInstanceInventory>,
			  C: MutableRepository<PlayerInstanceInventory>,
			  C: HasDirtyMarker<PlayerInstanceInventory>,
			  C: FablesInventoryRepository {
	override fun forOwner(playerInstance: PlayerInstance) = child.forOwner(playerInstance)
}
