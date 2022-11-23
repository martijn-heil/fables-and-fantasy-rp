package com.fablesfantasyrp.plugin.location.data.entity

import com.fablesfantasyrp.plugin.database.repository.KeyedRepository
import com.fablesfantasyrp.plugin.database.repository.MutableRepository
import com.fablesfantasyrp.plugin.database.repository.Repository
import com.fablesfantasyrp.plugin.playerinstance.data.entity.PlayerInstance

interface PlayerInstanceLocationRepository :
		Repository<PlayerInstanceLocation>,
		MutableRepository<PlayerInstanceLocation>,
		KeyedRepository<Int, PlayerInstanceLocation> {
			fun forOwner(playerInstance: PlayerInstance): PlayerInstanceLocation
}
