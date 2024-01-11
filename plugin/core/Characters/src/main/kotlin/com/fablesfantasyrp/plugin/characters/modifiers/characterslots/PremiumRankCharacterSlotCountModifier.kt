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
