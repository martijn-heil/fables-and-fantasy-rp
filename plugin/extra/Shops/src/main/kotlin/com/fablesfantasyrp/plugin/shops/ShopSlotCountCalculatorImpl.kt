package com.fablesfantasyrp.plugin.shops

import com.fablesfantasyrp.plugin.utils.getPermissionLevel
import org.bukkit.entity.Player

const val MAX_SHOP_SLOTS = 4

class ShopSlotCountCalculatorImpl : ShopSlotCountCalculator {
	override fun getShopSlots(player: Player): Int
		= player.getPermissionLevel(Permission.Slots, MAX_SHOP_SLOTS)
}
