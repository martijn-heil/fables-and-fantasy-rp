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
		displayItemService.removeDisplayItem(v.location)
	}

	override fun markDirty(v: Shop, what: String, oldValue: Any?, newValue: Any?) {
		super.markDirty(v, what, oldValue, newValue)

		if (what == "location") {
			if (oldValue != null) byLocation.remove(oldValue)
			if (newValue != null) byLocation[newValue as BlockIdentifier] = v
		}
	}
}
