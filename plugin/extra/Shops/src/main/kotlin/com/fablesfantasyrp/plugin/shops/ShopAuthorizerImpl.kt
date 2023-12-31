package com.fablesfantasyrp.plugin.shops

import com.fablesfantasyrp.plugin.profile.ProfileManager
import com.fablesfantasyrp.plugin.profile.data.entity.Profile
import com.fablesfantasyrp.plugin.shops.domain.entity.Shop
import org.bukkit.entity.Player

class ShopAuthorizerImpl(private val profileManager: ProfileManager) : ShopAuthorizer {
	override suspend fun mayEdit(shop: Shop, who: Profile): Boolean {
		val player = profileManager.getCurrentForProfile(who)
		return shop.owner == who || player?.hasPermission(Permission.Admin) == true
	}

	override suspend fun mayManagePublicShops(who: Player): Boolean {
		return who.hasPermission(Permission.Admin)
	}
}
