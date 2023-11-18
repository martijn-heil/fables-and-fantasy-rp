package com.fablesfantasyrp.plugin.fasttravel.data.entity

import com.fablesfantasyrp.plugin.database.repository.KeyedRepository
import com.fablesfantasyrp.plugin.database.repository.MutableRepository
import com.fablesfantasyrp.plugin.database.repository.Repository
import com.fablesfantasyrp.plugin.worldguardinterop.WorldGuardRegion

interface FastTravelLinkRepository :
		Repository<FastTravelLink>,
		MutableRepository<FastTravelLink>,
		KeyedRepository<Int, FastTravelLink> {
			fun forOriginRegion(region: WorldGuardRegion): FastTravelLink?
}
