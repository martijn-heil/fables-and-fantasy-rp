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
	private val byFromRegion = HashMap<WorldGuardRegion, FastTravelLink>()

	override fun init() {
		super.init()
		all().forEach { byFromRegion[it.from] = it }
	}

	override fun create(v: FastTravelLink): FastTravelLink {
		val created = super.create(v)
		byFromRegion[created.from] = created
		return created
	}

	override fun destroy(v: FastTravelLink) {
		super.destroy(v)
		byFromRegion.remove(v.from)
	}

	override fun forOriginRegion(region: WorldGuardRegion): FastTravelLink? {
		return byFromRegion[region]
	}
}
