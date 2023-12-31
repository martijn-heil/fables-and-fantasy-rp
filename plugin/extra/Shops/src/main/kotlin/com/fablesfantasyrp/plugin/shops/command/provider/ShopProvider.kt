package com.fablesfantasyrp.plugin.shops.command.provider

import com.fablesfantasyrp.plugin.database.command.SimpleAsyncEntityProvider
import com.fablesfantasyrp.plugin.shops.domain.entity.Shop
import com.fablesfantasyrp.plugin.shops.domain.repository.ShopRepository

class ShopProvider(shops: ShopRepository) : SimpleAsyncEntityProvider<Shop, ShopRepository>(shops) {
	override val entityName: String = "Shop"
}
