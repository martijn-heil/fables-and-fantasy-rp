/*
 * Fables and Fantasy RP kotlin plugins.
 * Copyright (C) 2024  Martijn Heil
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
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
