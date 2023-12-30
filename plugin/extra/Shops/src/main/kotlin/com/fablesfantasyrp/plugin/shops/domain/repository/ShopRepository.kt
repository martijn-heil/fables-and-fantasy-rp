package com.fablesfantasyrp.plugin.shops.domain.repository

import com.fablesfantasyrp.plugin.database.async.repository.AsyncKeyedRepository
import com.fablesfantasyrp.plugin.database.async.repository.AsyncMutableRepository
import com.fablesfantasyrp.plugin.database.entity.HasDestroyHandler
import com.fablesfantasyrp.plugin.shops.domain.entity.Shop
import com.fablesfantasyrp.plugin.utils.extensions.bukkit.BlockIdentifier

interface ShopRepository :
		HasDestroyHandler<Shop>,
	AsyncMutableRepository<Shop>,
	AsyncKeyedRepository<Int, Shop> {
	fun forLocation(location: BlockIdentifier): Shop?
}
