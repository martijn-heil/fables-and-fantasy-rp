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
package com.fablesfantasyrp.plugin.characters.modifiers.characterslots

import com.fablesfantasyrp.plugin.domain.premium.PremiumRank
import com.fablesfantasyrp.plugin.domain.premium.PremiumRankCalculator
import org.bukkit.entity.Player

class PremiumRankCharacterSlotCountModifier(private val premiumRankCalculator: PremiumRankCalculator) : CharacterSlotCountModifier {
	override fun calculateModifier(player: Player): Int {
		val rank = premiumRankCalculator.getRank(player)

		return when (rank) {
			PremiumRank.ADVENTURER -> 1
			PremiumRank.ELEMENTAL_NAVIGATOR -> 2
			PremiumRank.VOID_WALKER -> 2
			PremiumRank.HERALD_OF_LILITH -> 3
			else -> 0
		}
	}
}
