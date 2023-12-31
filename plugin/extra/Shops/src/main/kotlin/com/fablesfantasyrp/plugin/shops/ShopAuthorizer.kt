package com.fablesfantasyrp.plugin.shops

import com.fablesfantasyrp.plugin.profile.data.entity.Profile
import com.fablesfantasyrp.plugin.shops.domain.entity.Shop
import org.bukkit.entity.Player

interface ShopAuthorizer {
	suspend fun mayEdit(shop: Shop, who: Profile): Boolean
	suspend fun mayManagePublicShops(who: Player): Boolean
}
