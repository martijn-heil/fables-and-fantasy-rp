package com.fablesfantasyrp.plugin.inventory.data.entity

import com.fablesfantasyrp.plugin.database.repository.KeyedRepository
import com.fablesfantasyrp.plugin.database.repository.MutableRepository
import com.fablesfantasyrp.plugin.database.repository.Repository
import com.fablesfantasyrp.plugin.playerinstance.data.entity.PlayerInstance

interface FablesInventoryRepository :
		Repository<PlayerInstanceInventory>,
		MutableRepository<PlayerInstanceInventory>,
		KeyedRepository<Int, PlayerInstanceInventory> {
			fun forOwner(playerInstance: PlayerInstance): PlayerInstanceInventory
}
