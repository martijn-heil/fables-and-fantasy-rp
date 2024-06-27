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
package com.fablesfantasyrp.plugin.domain.test.premium

import com.fablesfantasyrp.plugin.domain.premium.PremiumRank
import com.fablesfantasyrp.plugin.domain.premium.PremiumRankCalculatorImpl
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import net.milkbowl.vault.permission.Permission
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin
import org.bukkit.plugin.RegisteredServiceProvider
import org.bukkit.plugin.ServicePriority
import org.bukkit.plugin.ServicesManager
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

internal class PremiumRankCalculatorTest {

	private fun setup(playerGroups: Set<String>) {
		mockkStatic(Bukkit::class)

		val plugin = mockk<Plugin>()
		val permission = mockk<Permission>()
		val servicesManager = mockk<ServicesManager>()

		every { Bukkit.getServicesManager() } returns servicesManager
		every { servicesManager.getRegistration(Permission::class.java) } returns
			RegisteredServiceProvider(
				Permission::class.java,
				permission,
				ServicePriority.Normal,
				plugin
			)
		every { permission.getPlayerGroups(any()) } returns playerGroups.toTypedArray()
	}

	private fun picksHighest(groups: Set<String>, expected: PremiumRank?) {
		setup(groups)
		val player = mockk<Player>()

		val calculator = PremiumRankCalculatorImpl()

		assertEquals(expected, calculator.getRank(player))
	}

	@Test
	fun `always picks highest rank 1`() {
		picksHighest(setOf(
			"donator-voidwalker",
			"donator-elementalnavigator",
			"donator-heraldoflilith",
			"donator-adventurer",
			"default"
		), PremiumRank.HERALD_OF_LILITH)
	}

	@Test
	fun `always picks highest rank 2`() {
		picksHighest(setOf(
			"donator-elementalnavigator",
			"donator-voidwalker",
			"donator-adventurer",
			"wanderer"
		), PremiumRank.VOID_WALKER)
	}

	@Test
	fun `always picks highest rank 3`() {
		picksHighest(setOf(
			"donator-elementalnavigator",
			"donator-adventurer",
			"default"
		), PremiumRank.ELEMENTAL_NAVIGATOR)
	}

	@Test
	fun `always picks highest rank 4`() {
		picksHighest(setOf(
			"default",
			"donator-adventurer"
		), PremiumRank.ADVENTURER)
	}

	@Test
	fun `returns null if no premium rank`() {
		picksHighest(setOf(
			"default", "t-tech-assistant"
		), null)
	}
}
