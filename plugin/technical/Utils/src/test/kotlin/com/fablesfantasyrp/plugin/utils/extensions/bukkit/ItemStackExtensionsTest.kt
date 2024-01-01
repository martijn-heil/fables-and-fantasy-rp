package com.fablesfantasyrp.plugin.utils.extensions.bukkit

import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import net.minecraft.SharedConstants
import net.minecraft.server.Bootstrap
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.inventory.ItemFactory
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.Damageable
import org.bukkit.inventory.meta.ItemMeta
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import kotlin.test.assertEquals

internal class ItemStackExtensionsTest {
	init {
		mockkStatic(Bukkit::class)

		val itemFactory = mockk<ItemFactory>()
		val itemMeta = mockk<Damageable>()

		every { Bukkit.getItemFactory() } returns itemFactory
		every { itemMeta.damage } returns 0
		every { itemFactory.getItemMeta(any()) } returns itemMeta
		every { itemFactory.equals(any(), any()) } answers { firstArg<ItemMeta?>() == secondArg<ItemMeta?>() }

		SharedConstants.tryDetectVersion()
		Bootstrap.bootStrap()
	}

	@ParameterizedTest
	@CsvSource(
		"16,15,1",
		"16,16,1",
		"16,17,2",
		"64,63,1",
		"64,64,1",
		"64,65,2",
		"64,127,2",
		"64,128,2",
		"64,129,3")
	fun testSplitStacks(maxStackSizeString: String, sumString: String, expectedStackCountString: String) {
		val maxStackSize = maxStackSizeString.toInt()
		val sum = sumString.toInt()
		val expectedStackCount = expectedStackCountString.toInt()

		val stack = ItemStack(Material.DIRT, sum)
		val result = stack.splitStacks(maxStackSize)

		assertEquals(expectedStackCount, result.size)
		assertEquals(sum, result.sumOf { it.amount })
	}
}
