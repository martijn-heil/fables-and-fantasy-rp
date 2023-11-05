package com.fablesfantasyrp.plugin.lodestones.domain.repository

import com.fablesfantasyrp.plugin.database.entity.MassivelyCachingEntityRepository
import com.fablesfantasyrp.plugin.lodestones.domain.entity.LodestoneBanner
import com.fablesfantasyrp.plugin.lodestones.domain.mapper.LodestoneBannerMapper
import com.fablesfantasyrp.plugin.utils.extensions.bukkit.BlockIdentifier

class LodestoneBannerRepositoryImpl(child: LodestoneBannerMapper, private val lodestones: LodestoneRepository)
	: MassivelyCachingEntityRepository<Int, LodestoneBanner, LodestoneBannerMapper>(child), LodestoneBannerRepository {
    private val byLocation = HashMap<BlockIdentifier, LodestoneBanner>()
	private val byLodestone = HashMap<Int, LodestoneBanner>()

	override fun init() {
		super.init()
		strongCache.forEach {
			byLocation[it.location] = it
			byLodestone[it.lodestone.id] = it
		}
		lodestones.onDestroy { byLodestone[it.id]?.let { banner -> this.destroy(banner) } }
	}

	override fun forLocation(location: BlockIdentifier): LodestoneBanner? {
		return byLocation[location]
	}

	override fun create(v: LodestoneBanner): LodestoneBanner {
		val created = super.create(v)
		byLocation[created.location] = created
		byLodestone[created.lodestone.id] = created
		return created
	}

	override fun forId(id: Int): LodestoneBanner? {
		val found = super.forId(id)
		if (found != null) {
			byLocation[found.location] = found
			byLodestone[found.lodestone.id] = found
		}
		return found
	}

	override fun destroy(v: LodestoneBanner) {
		super.destroy(v)
		byLocation.remove(v.location)
		byLodestone.remove(v.lodestone.id)
		v.isDestroyed = true
	}

	override fun markDirty(v: LodestoneBanner, what: String, oldValue: Any?, newValue: Any?) {
		super.markDirty(v, what, oldValue, newValue)

		if (what == "location") {
			if (oldValue != null) byLocation.remove(oldValue)
			if (newValue != null) byLocation[newValue as BlockIdentifier] = v
		}
	}
}
