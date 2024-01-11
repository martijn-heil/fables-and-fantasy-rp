package com.fablesfantasyrp.plugin.domain.premium

import org.bukkit.entity.Player

interface PremiumRankCalculator {
	fun getRank(player: Player): PremiumRank?
}
