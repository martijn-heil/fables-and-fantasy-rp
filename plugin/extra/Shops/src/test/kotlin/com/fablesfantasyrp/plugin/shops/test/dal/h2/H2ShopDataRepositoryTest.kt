package com.fablesfantasyrp.plugin.shops.test.dal.h2

import com.fablesfantasyrp.plugin.database.testfixtures.BaseH2RepositoryTest
import com.fablesfantasyrp.plugin.shops.dal.h2.H2ShopDataRepository
import com.fablesfantasyrp.plugin.shops.dal.model.ShopData
import com.fablesfantasyrp.plugin.utils.extensions.bukkit.BlockIdentifier
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
import org.junit.jupiter.api.Test
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.*
import kotlin.test.assertEquals

internal class H2ShopDataRepositoryTest : BaseH2RepositoryTest("FABLES_SHOPS") {
	private val repository = H2ShopDataRepository(dataSource)

	private val simpleEntity = ShopData(
		location = BlockIdentifier(UUID.randomUUID(), 0, 0, 0),
		amount = 0,
		buyPrice = 0,
		sellPrice = 0,
		item = ItemStack(Material.COBBLESTONE),
		lastActive = Instant.now().truncatedTo(ChronoUnit.SECONDS),
		owner = 1,
		stock = 0,
	)

	private val maxEntity = simpleEntity.copy(
		amount = Int.MAX_VALUE,
		buyPrice = Int.MAX_VALUE,
		sellPrice = Int.MAX_VALUE,
		owner = Int.MAX_VALUE,
		stock = Int.MAX_VALUE
	)

	private val minEntity = simpleEntity.copy(
		amount = Int.MIN_VALUE,
		buyPrice = Int.MIN_VALUE,
		sellPrice = Int.MIN_VALUE,
		owner = Int.MIN_VALUE,
		stock = Int.MIN_VALUE
	)

	init {
		mockkStatic(Bukkit::class)

		// All this mocking is necessary for SerializableItemStack to work because it uses NMS internals
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
	fun testCreateSimple() {
		val created1 = repository.create(simpleEntity)
		assertEquals(created1, repository.forId(created1.id))
	}

	@Test
	fun testCreateMaxValues() {
		val created = repository.create(maxEntity)
		assertEquals(created, repository.forId(created.id))
	}

	@Test
	fun testCreateMinValues() {
		val created = repository.create(minEntity)
		assertEquals(created, repository.forId(created.id))
	}

	@Test
	fun testCreateNullability() {
		val created = repository.create(simpleEntity.copy(owner = null))
		assertEquals(created, repository.forId(created.id))
	}

	@Test
	fun testUpdateSimple() {
		val created = repository.create(simpleEntity)
		assertEquals(created, repository.forId(created.id))

		val updated = created.copy(
			amount = created.amount + 1,
			buyPrice = created.amount + 1,
			sellPrice = created.sellPrice + 1,
			owner = created.owner!! + 1,
			stock = created.stock + 1,
		)
		repository.update(updated)

		assertEquals(updated, repository.forId(updated.id))
	}

	@Test
	fun testUpdateMaxValues() {
		val created = repository.create(maxEntity)
		assertEquals(created, repository.forId(created.id))

		val updated = created.copy(
			amount = created.amount - 1,
			buyPrice = created.amount - 1,
			sellPrice = created.sellPrice - 1,
			owner = created.owner!! - 1,
			stock = created.stock - 1,
		)
		repository.update(updated)

		assertEquals(updated, repository.forId(updated.id))
	}

	@Test
	fun testUpdateMinValues() {
		val created = repository.create(minEntity)
		assertEquals(created, repository.forId(created.id))

		val updated = created.copy(
			amount = created.amount + 1,
			buyPrice = created.amount + 1,
			sellPrice = created.sellPrice + 1,
			owner = created.owner!! + 1,
			stock = created.stock + 1,
		)
		repository.update(updated)

		assertEquals(updated, repository.forId(updated.id))
	}

	@Test
	fun testUpdateNullability() {
		val created = repository.create(simpleEntity.copy(owner = 1))

		val updated = created.copy(owner = null)
		repository.update(updated)

		assertEquals(updated, repository.forId(updated.id))
	}
}
