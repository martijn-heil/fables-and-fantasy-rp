package com.fablesfantasyrp.plugin.shops.command.provider

import com.fablesfantasyrp.caturix.parametric.AbstractModule
import com.fablesfantasyrp.plugin.shops.domain.entity.Shop
import com.fablesfantasyrp.plugin.shops.domain.repository.ShopRepository

class ShopModule(private val shops: ShopRepository) : AbstractModule() {
	override fun configure() {
		bind(Shop::class.java).toProvider(ShopProvider(shops))
	}
}
