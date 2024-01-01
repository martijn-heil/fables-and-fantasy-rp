package com.fablesfantasyrp.plugin.utils.domain.premium

import com.fablesfantasyrp.plugin.utils.Services
import net.milkbowl.vault.permission.Permission
import org.bukkit.entity.Player

class PremiumRankCalculatorImpl : PremiumRankCalculator {
	override fun getRank(player: Player): PremiumRank? {
		val permission = Services.get<Permission>()
		val groups = permission.getPlayerGroups(player).toHashSet()

		return when {
			groups.contains("donator-heraldoflilith") -> PremiumRank.HERALD_OF_LILITH
			groups.contains("donator-voidwalker") -> PremiumRank.VOID_WALKER
			groups.contains("donator-elementalnavigator") -> PremiumRank.ELEMENTAL_NAVIGATOR
			groups.contains("donator-adventurer") -> PremiumRank.ADVENTURER
			groups.contains("donator-explorer") -> PremiumRank.EXPLORER
			else -> null
		}
	}
}
