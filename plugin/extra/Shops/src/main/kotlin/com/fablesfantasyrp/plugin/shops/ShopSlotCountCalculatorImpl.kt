package com.fablesfantasyrp.plugin.shops

import com.fablesfantasyrp.plugin.utils.domain.premium.PremiumRank
import com.fablesfantasyrp.plugin.utils.domain.premium.PremiumRankCalculator
import org.bukkit.entity.Player

class ShopSlotCountCalculatorImpl(private val premiumRankCalculator: PremiumRankCalculator) : ShopSlotCountCalculator {
	override fun getShopSlots(player: Player): Int?
		= when (premiumRankCalculator.getRank(player)) {
			PremiumRank.HERALD_OF_LILITH -> null
			PremiumRank.VOID_WALKER -> 60
			PremiumRank.ELEMENTAL_NAVIGATOR -> 40
			PremiumRank.ADVENTURER -> 25
			PremiumRank.EXPLORER -> 15
			null -> 10
		}
}
