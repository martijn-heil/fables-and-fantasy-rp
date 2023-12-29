package com.fablesfantasyrp.plugin.shops

import org.bukkit.entity.Player

interface ShopSlotCountCalculator {
	fun getShopSlots(player: Player): Int
}
