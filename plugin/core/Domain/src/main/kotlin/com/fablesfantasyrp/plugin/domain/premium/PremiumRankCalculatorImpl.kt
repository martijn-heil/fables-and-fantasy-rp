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
package com.fablesfantasyrp.plugin.domain.premium

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
