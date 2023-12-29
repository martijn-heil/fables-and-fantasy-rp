package com.fablesfantasyrp.plugin.shops.domain.repository

import com.fablesfantasyrp.plugin.database.entity.HasDestroyHandler
import com.fablesfantasyrp.plugin.database.sync.repository.KeyedRepository
import com.fablesfantasyrp.plugin.database.sync.repository.MutableRepository
import com.fablesfantasyrp.plugin.shops.domain.entity.Shop
import com.fablesfantasyrp.plugin.utils.extensions.bukkit.BlockIdentifier

interface ShopRepository :
		HasDestroyHandler<Shop>,
	MutableRepository<Shop>,
	KeyedRepository<Int, Shop> {
	fun forLocation(location: BlockIdentifier): Shop?
}
