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
