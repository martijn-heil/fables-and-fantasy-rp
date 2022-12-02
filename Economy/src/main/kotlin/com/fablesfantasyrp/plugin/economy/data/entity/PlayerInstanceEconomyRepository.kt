package com.fablesfantasyrp.plugin.economy.data.entity

import com.fablesfantasyrp.plugin.database.repository.KeyedRepository
import com.fablesfantasyrp.plugin.database.repository.MutableRepository
import com.fablesfantasyrp.plugin.database.repository.Repository
import com.fablesfantasyrp.plugin.playerinstance.data.entity.PlayerInstance

interface PlayerInstanceEconomyRepository :
		Repository<PlayerInstanceEconomy>,
		MutableRepository<PlayerInstanceEconomy>,
		KeyedRepository<Int, PlayerInstanceEconomy> {
			fun forPlayerInstance(playerInstance: PlayerInstance): PlayerInstanceEconomy
}
