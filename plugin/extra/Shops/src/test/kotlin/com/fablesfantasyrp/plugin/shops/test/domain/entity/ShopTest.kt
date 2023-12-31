package com.fablesfantasyrp.plugin.shops.test.domain.entity

import com.fablesfantasyrp.plugin.economy.data.entity.ProfileEconomy
import com.fablesfantasyrp.plugin.profile.data.entity.Profile
import com.fablesfantasyrp.plugin.shops.domain.entity.Shop
import com.fablesfantasyrp.plugin.utils.extensions.bukkit.BlockIdentifier
import com.fablesfantasyrp.plugin.utils.extensions.bukkit.countSimilar
import com.fablesfantasyrp.plugin.utils.extensions.bukkit.deposit
import com.fablesfantasyrp.plugin.utils.extensions.bukkit.withdrawSimilar
import com.fablesfantasyrp.plugin.utils.validation.CommandValidationException
import io.mockk.*
import net.kyori.adventure.text.Component
import net.minecraft.SharedConstants
import net.minecraft.server.Bootstrap
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.OfflinePlayer
import org.bukkit.Server
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemFactory
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.PlayerInventory
import org.bukkit.inventory.meta.Damageable
import org.bukkit.inventory.meta.ItemMeta
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import java.time.Instant
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

internal class ShopTest {
	init {
		mockkStatic(Bukkit::class)
		mockkStatic("com.fablesfantasyrp.plugin.utils.extensions.bukkit.InventoryExtensionsKt")

		val server = mockk<Server>()
		val offlinePlayer = mockk<OfflinePlayer>()

		every { Bukkit.getServer() } returns server
		every { Bukkit.getOfflinePlayer(any<UUID>()) } returns offlinePlayer

		val itemFactory = mockk<ItemFactory>()
		val itemMeta = mockk<Damageable>()

		every { Bukkit.getItemFactory() } returns itemFactory
		every { itemMeta.damage } returns 0
		every { itemFactory.getItemMeta(any()) } returns itemMeta
		every { itemFactory.equals(any(), any()) } answers { firstArg<ItemMeta?>() == secondArg<ItemMeta?>() }

		SharedConstants.tryDetectVersion()
		Bootstrap.bootStrap()
	}

	@Test
	fun testIsPublic() {
		val shop = Shop(
			location = BlockIdentifier(UUID.randomUUID(), 0, 0, 0),
			amount = 1,
			item = ItemStack(Material.COBBLESTONE),
			buyPrice = 0,
			sellPrice = 0,
			lastActive = Instant.now(),
			stock = 0,
			owner = null
		)

		assertTrue(shop.isPublic)
	}

	@Test
	fun testIsNotPublic() {
		val shop = Shop(
			location = BlockIdentifier(UUID.randomUUID(), 0, 0, 0),
			amount = 1,
			item = ItemStack(Material.COBBLESTONE),
			buyPrice = 0,
			sellPrice = 0,
			lastActive = Instant.now(),
			stock = 0,
			owner = Profile(
				owner = null,
				isActive = true,
				description = null
			)
		)

		assertFalse(shop.isPublic)
	}

	@ParameterizedTest
	@CsvSource(
		"50,100,20,130",
		"10,100,10,100")
	fun testBuy(customerMoneyString: String,
				ownerMoneyString: String,
				expectedCustomerMoneyString: String,
				expectedOwnerMoneyString: String) {
		val customerMoney = customerMoneyString.toInt()
		val ownerMoney = ownerMoneyString.toInt()
		val expectedCustomerMoney = expectedCustomerMoneyString.toInt()
		val expectedOwnerMoney = expectedOwnerMoneyString.toInt()

		val customerPlayer = mockk<Player>()
		val customerPlayerInventory = mockk<PlayerInventory>()

		every { customerPlayer.inventory } returns customerPlayerInventory
		every { customerPlayer.sendMessage(any<String>()) } just runs
		every { customerPlayer.sendMessage(any<Component>()) } just runs
		every { customerPlayerInventory.deposit(any()) } returns null

		val customerEconomy = ProfileEconomy(
			id = 1,
			money = customerMoney,
			bankMoney = 0
		)
		val ownerEconomy = ProfileEconomy(
			id = 2,
			money = ownerMoney,
			bankMoney = 0
		)

		val shop = Shop(
			location = BlockIdentifier(UUID.randomUUID(), 0, 0, 0),
			amount = 1,
			item = ItemStack(Material.COBBLESTONE),
			buyPrice = 30,
			sellPrice = 0,
			lastActive = Instant.now(),
			stock = 1,
			owner = Profile(
				owner = null,
				isActive = true,
				description = null
			)
		)

		var success = true
		try {
			shop.buy(customerPlayer, customerEconomy, ownerEconomy)
		} catch (_: CommandValidationException) {
			success = false
		}

		if (success) {
			verify(exactly = 1) { customerPlayerInventory.deposit(match { it.type == Material.COBBLESTONE && it.amount == 1 }) }
		} else {
			verify(exactly = 0) { customerPlayerInventory.deposit(match { it.type == Material.COBBLESTONE && it.amount == 1 }) }
		}

		assertEquals(expectedCustomerMoney, customerEconomy.money)
		assertEquals(expectedOwnerMoney, ownerEconomy.money)
	}

	@ParameterizedTest
	@CsvSource(
		"1,1,50,100,2,80,70", 	// Success
		"1,0,50,100,1,50,100", 	// Not enough available in customer's inventory
		"1,1,50,29,1,50,29"		// Vendor does not have enough funds
	)
	fun testSell(stockString: String,
				 customerAmountAvailableString: String,
				 customerMoneyString: String,
				 ownerMoneyString: String,
				 expectedStockString: String,
				 expectedCustomerMoneyString: String,
				 expectedOwnerMoneyString: String) {
		val stock = stockString.toInt()
		val customerAmountAvailable = customerAmountAvailableString.toInt()
		val customerMoney = customerMoneyString.toInt()
		val ownerMoney = ownerMoneyString.toInt()
		val expectedStock = expectedStockString.toInt()
		val expectedCustomerMoney = expectedCustomerMoneyString.toInt()
		val expectedOwnerMoney = expectedOwnerMoneyString.toInt()

		val customerPlayer = mockk<Player>()
		val customerPlayerInventory = mockk<PlayerInventory>()

		every { customerPlayer.inventory } returns customerPlayerInventory
		every { customerPlayer.sendMessage(any<String>()) } just runs
		every { customerPlayer.sendMessage(any<Component>()) } just runs
		every { customerPlayerInventory.countSimilar(any()) } returns customerAmountAvailable
		every { customerPlayerInventory.withdrawSimilar(any(), any()) } answers { secondArg<ItemStack>().asQuantity(thirdArg()) }

		val customerEconomy = ProfileEconomy(
			id = 1,
			money = customerMoney,
			bankMoney = 0
		)
		val ownerEconomy = ProfileEconomy(
			id = 2,
			money = ownerMoney,
			bankMoney = 0
		)

		val shop = Shop(
			location = BlockIdentifier(UUID.randomUUID(), 0, 0, 0),
			amount = 1,
			item = ItemStack(Material.COBBLESTONE),
			buyPrice = 0,
			sellPrice = 30,
			lastActive = Instant.now(),
			stock = stock,
			owner = Profile(
				owner = null,
				isActive = true,
				description = null
			)
		)

		var success = true
		try {
			shop.sell(customerPlayer, customerEconomy, ownerEconomy)
		} catch (_: CommandValidationException) {
			success = false
		}

		if (success) {
			verify(exactly = 1) { customerPlayerInventory.withdrawSimilar(match { it.type == Material.COBBLESTONE }, shop.amount) }
		} else {
			verify(exactly = 0) { customerPlayerInventory.withdrawSimilar(match { it.type == Material.COBBLESTONE }, shop.amount) }
		}

		assertEquals(expectedStock, shop.stock)
		assertEquals(expectedCustomerMoney, customerEconomy.money)
		assertEquals(expectedOwnerMoney, ownerEconomy.money)
	}
}
