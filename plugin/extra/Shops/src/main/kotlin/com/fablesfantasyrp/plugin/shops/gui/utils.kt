package com.fablesfantasyrp.plugin.shops.gui

import com.fablesfantasyrp.plugin.characters.shortName
import com.fablesfantasyrp.plugin.shops.domain.entity.Shop

suspend fun getShopTitle(shop: Shop): String
	= if (shop.isPublic) {
		"Public shop #${shop.id}"
	} else {
		"${shop.owner!!.shortName()}'s shop #${shop.id}"
	}
