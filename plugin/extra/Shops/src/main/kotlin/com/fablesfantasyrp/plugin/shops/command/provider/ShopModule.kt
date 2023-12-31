package com.fablesfantasyrp.plugin.shops.command.provider

import com.fablesfantasyrp.caturix.parametric.AbstractModule
import com.fablesfantasyrp.caturix.parametric.Provider
import com.fablesfantasyrp.plugin.shops.domain.entity.Shop
import com.fablesfantasyrp.plugin.shops.domain.repository.ShopRepository
import com.fablesfantasyrp.plugin.utils.command.OrVisual
import org.bukkit.entity.Player

class ShopModule(private val shops: ShopRepository,
				 private val playerSenderProvider: Provider<Player>) : AbstractModule() {
	override fun configure() {
		val shopProvider = ShopProvider(shops)

		bind(Shop::class.java).toProvider(shopProvider)
		bind(Shop::class.java).annotatedWith(OrVisual::class.java).toProvider(OrVisualShopProvider(shops, shopProvider,playerSenderProvider))
	}
}
