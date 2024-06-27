/*
 * Fables and Fantasy RP kotlin plugins.
 * Copyright (C) 2024  Martijn Heil
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.fablesfantasyrp.plugin.shops

import com.fablesfantasyrp.plugin.domain.premium.PremiumRank
import com.fablesfantasyrp.plugin.domain.premium.PremiumRankCalculator
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
