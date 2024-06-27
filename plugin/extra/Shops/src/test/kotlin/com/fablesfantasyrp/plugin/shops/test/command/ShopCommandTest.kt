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
package com.fablesfantasyrp.plugin.shops.test.command

import com.fablesfantasyrp.caturix.util.auth.AuthorizationException
import com.fablesfantasyrp.plugin.profile.data.entity.Profile
import com.fablesfantasyrp.plugin.shops.ShopAuthorizer
import com.fablesfantasyrp.plugin.shops.ShopSlotCountCalculator
import com.fablesfantasyrp.plugin.shops.command.ShopCommand
import com.fablesfantasyrp.plugin.shops.domain.repository.ShopRepository
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.block.Block
import org.bukkit.entity.Player
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.*

internal class ShopCommandTest {
	@Test
	fun `test-create-public-unauthorized`() {
		runBlocking {
			val sender = mockk<Player>()
			val block = mockk<Block>()
			val world = mockk<World>()
			val shops = mockk<ShopRepository>()
			val authorizer = mockk<ShopAuthorizer>()
			val slotCountCalculator = mockk<ShopSlotCountCalculator>()

			val command = ShopCommand(shops, authorizer, slotCountCalculator)

			val worldUuid = UUID.randomUUID()
			val profile = Profile(
				id = 1,
				isActive = true,
				description = null,
				owner = null,
			)
			every { world.uid } returns worldUuid
			coEvery { authorizer.mayManagePublicShops(any()) } returns false
			every { sender.getTargetBlockExact(any()) } returns block

			val location = Location(world, 0.00, 0.00, 0.00)

			every { block.location } returns location

			assertThrows<AuthorizationException> {
				command.create(sender, true, profile)
			}
		}
	}
}
