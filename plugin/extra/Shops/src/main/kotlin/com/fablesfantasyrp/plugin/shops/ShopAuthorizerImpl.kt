package com.fablesfantasyrp.plugin.shops

import com.fablesfantasyrp.plugin.profile.data.entity.Profile
import com.fablesfantasyrp.plugin.shops.domain.entity.Shop

class ShopAuthorizerImpl() : ShopAuthorizer {
	override suspend fun mayEdit(shop: Shop, who: Profile?) {
		TODO("Not yet implemented")
	}
}
