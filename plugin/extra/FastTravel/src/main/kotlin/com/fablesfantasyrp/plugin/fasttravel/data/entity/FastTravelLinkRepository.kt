package com.fablesfantasyrp.plugin.fasttravel.data.entity

import com.fablesfantasyrp.plugin.database.sync.repository.KeyedRepository
import com.fablesfantasyrp.plugin.database.sync.repository.MutableRepository
import com.fablesfantasyrp.plugin.database.sync.repository.Repository
import com.fablesfantasyrp.plugin.worldguardinterop.WorldGuardRegion

interface FastTravelLinkRepository :
		Repository<FastTravelLink>,
	MutableRepository<FastTravelLink>,
	KeyedRepository<Int, FastTravelLink> {
			fun forOriginRegion(region: WorldGuardRegion): FastTravelLink?
}
