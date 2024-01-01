package com.fablesfantasyrp.plugin.utils.domain.premium

import org.bukkit.entity.Player

interface PremiumRankCalculator {
	fun getRank(player: Player): PremiumRank?
}
