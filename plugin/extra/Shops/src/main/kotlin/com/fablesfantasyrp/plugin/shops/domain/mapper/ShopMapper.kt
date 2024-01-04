package com.fablesfantasyrp.plugin.shops.domain.mapper

import com.fablesfantasyrp.plugin.database.CacheMarker
import com.fablesfantasyrp.plugin.database.async.repository.base.AsyncMappingRepository
import com.fablesfantasyrp.plugin.database.model.HasCacheMarker
import com.fablesfantasyrp.plugin.database.model.HasDirtyMarker
import com.fablesfantasyrp.plugin.database.repository.DirtyMarker
import com.fablesfantasyrp.plugin.profile.data.entity.Profile
import com.fablesfantasyrp.plugin.profile.data.entity.ProfileRepository
import com.fablesfantasyrp.plugin.shops.dal.model.ShopData
import com.fablesfantasyrp.plugin.shops.dal.repository.ShopDataRepository
import com.fablesfantasyrp.plugin.shops.domain.entity.Shop
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ShopMapper(child: ShopDataRepository, private val profiles: ProfileRepository)
	: AsyncMappingRepository<Int, ShopData, Shop, ShopDataRepository>(child),
	HasDirtyMarker<Shop>, HasCacheMarker<Shop> {

	override var dirtyMarker: DirtyMarker<Shop>? = null
	override var cacheMarker: CacheMarker<Shop>? = null

	override fun convertFromChild(v: ShopData) = Shop(
		id = v.id,
		location = v.location,
		owner = v.owner?.let { profiles.forId(v.owner)!! },
		item = v.item,
		lastActive = v.lastActive,
		amount = v.amount,
		buyPrice = v.buyPrice,
		sellPrice = v.sellPrice,
		stock = v.stock,
		dirtyMarker = dirtyMarker
	)

	override fun convertToChild(v: Shop) = ShopData(
		id = v.id,
		location = v.location,
		owner = v.owner?.id,
		item = v.item,
		lastActive = v.lastActive,
		amount = v.amount,
		buyPrice = v.buyPrice,
		sellPrice = v.sellPrice,
		stock = v.stock,
	)

	suspend fun forOwner(owner: Profile): Collection<Shop>
		= withContext(Dispatchers.IO) { child.forOwner(owner.id) }.map { convertFromChild(it) }
}
