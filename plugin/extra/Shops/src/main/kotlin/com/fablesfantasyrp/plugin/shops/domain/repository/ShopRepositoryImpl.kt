package com.fablesfantasyrp.plugin.shops.domain.repository

import com.fablesfantasyrp.plugin.database.entity.MassivelyCachingEntityRepository
import com.fablesfantasyrp.plugin.shops.domain.entity.Shop
import com.fablesfantasyrp.plugin.shops.domain.mapper.ShopMapper
import com.fablesfantasyrp.plugin.utils.extensions.bukkit.BlockIdentifier

class ShopRepositoryImpl(child: ShopMapper) : MassivelyCachingEntityRepository<Int, Shop, ShopMapper>(child), ShopRepository {
    private val byLocation = HashMap<BlockIdentifier, Shop>()

	override fun init() {
		super.init()
		strongCache.forEach { byLocation[it.location] = it }
	}

	override fun forLocation(location: BlockIdentifier): Shop? {
		return byLocation[location]
	}

	override fun create(v: Shop): Shop {
		val created = super.create(v)
		byLocation[created.location] = created
		return created
	}

	override fun forId(id: Int): Shop? {
		val found = super.forId(id)
		if (found != null) byLocation[found.location] = found
		return found
	}

	override fun destroy(v: Shop) {
		super.destroy(v)
		byLocation.remove(v.location)
		v.isDestroyed = true
	}

	override fun markDirty(v: Shop, what: String, oldValue: Any?, newValue: Any?) {
		super.markDirty(v, what, oldValue, newValue)

		if (what == "location") {
			if (oldValue != null) byLocation.remove(oldValue)
			if (newValue != null) byLocation[newValue as BlockIdentifier] = v
		}
	}
}
