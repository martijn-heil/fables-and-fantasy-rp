package com.fablesfantasyrp.plugin.shops.domain.repository

import com.fablesfantasyrp.plugin.database.async.repository.base.AsyncTypicalRepository
import com.fablesfantasyrp.plugin.profile.data.entity.Profile
import com.fablesfantasyrp.plugin.shops.domain.entity.Shop
import com.fablesfantasyrp.plugin.shops.domain.mapper.ShopMapper
import com.fablesfantasyrp.plugin.shops.frunBlocking
import com.fablesfantasyrp.plugin.shops.service.DisplayItemService
import com.fablesfantasyrp.plugin.utils.extensions.bukkit.BlockIdentifier

class ShopRepositoryImpl(child: ShopMapper,
						 private val displayItemService: DisplayItemService)
	: AsyncTypicalRepository<Int, Shop, ShopMapper>(child), ShopRepository {
    private val byLocation = HashMap<BlockIdentifier, Shop>()

	override fun init() {
		super.init()
		frunBlocking { all().forEach { markStrong(it) } }
		strongCache.forEach { byLocation[it.location] = it }
	}

	override fun forLocation(location: BlockIdentifier): Shop? {
		return byLocation[location]
	}

	override suspend fun forOwner(owner: Profile): Collection<Shop> {
		return deduplicate(child.forOwner(owner))
	}

	override suspend fun create(v: Shop): Shop {
		require(forLocation(v.location) == null)
		val created = super.create(v)
		byLocation[created.location] = created
		displayItemService.spawnDisplayItem(v.location, v.item)
		return created
	}

	override suspend fun forId(id: Int): Shop? {
		val found = super.forId(id)
		if (found != null) byLocation[found.location] = found
		return found
	}

	override suspend fun destroy(v: Shop) {
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
