package com.fablesfantasyrp.plugin.fasttravel.data.entity

import com.fablesfantasyrp.plugin.database.entity.MassivelyCachingEntityRepository
import com.fablesfantasyrp.plugin.database.model.HasDirtyMarker
import com.fablesfantasyrp.plugin.database.sync.repository.KeyedRepository
import com.fablesfantasyrp.plugin.database.sync.repository.MutableRepository
import com.fablesfantasyrp.plugin.worldguardinterop.WorldGuardRegion

class EntityFastTravelLinkRepository<C>(child: C) : MassivelyCachingEntityRepository<Int, FastTravelLink, C>(child), FastTravelLinkRepository
		where C: KeyedRepository<Int, FastTravelLink>,
			  C: MutableRepository<FastTravelLink>,
			  C: HasDirtyMarker<FastTravelLink>,
			  C: FastTravelLinkRepository {

	override fun forOriginRegion(region: WorldGuardRegion): FastTravelLink? {
		val childResult = child.forOriginRegion(region) ?: return null
		return this.forId(childResult.id)
	}
}
