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
package com.fablesfantasyrp.plugin.inventory.test.dal.h2

import com.fablesfantasyrp.plugin.database.testfixtures.BaseH2RepositoryTest
import com.fablesfantasyrp.plugin.inventory.PassthroughInventory
import com.fablesfantasyrp.plugin.inventory.PassthroughPlayerInventory
import com.fablesfantasyrp.plugin.inventory.dal.h2.H2ProfileInventoryDataRepository
import com.fablesfantasyrp.plugin.inventory.dal.model.ProfileInventoryData
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
import kotlin.test.assertEquals

internal class H2ProfileInventoryDataRepositoryTest : BaseH2RepositoryTest("FABLES_INVENTORY") {
	private val repository = H2ProfileInventoryDataRepository(dataSource)
	private fun simpleEntity(id: Int) = ProfileInventoryData(
		id = id,
		inventory = PassthroughPlayerInventory.createEmpty(),
		enderChest = PassthroughInventory(arrayOfNulls(27))
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
		val entity = simpleEntity(1).apply {
			inventory.itemInMainHand = ItemStack(Material.COBBLESTONE)
			enderChest[9] = ItemStack(Material.GLASS)
		}

		val created = repository.create(entity)
		val retrieved = repository.forId(created.id)!!

		assertEquals(entity.inventory.itemInMainHand, retrieved.inventory.itemInMainHand)
		assertEquals(entity.enderChest[9], retrieved.enderChest[9])
	}

	@Test
	fun testForOwnerNew() {
		val created = repository.forOwner(2)
		val retrieved = repository.forId(created.id)

		assertEquals(created.id, retrieved?.id)
	}

	@Test
	fun testForOwnerExisting() {
		val entity = simpleEntity(3).apply {
			inventory.itemInMainHand = ItemStack(Material.DIAMOND)
		}
		repository.create(entity)

		val existing = repository.forOwner(3)

		assertEquals(entity.inventory.itemInMainHand, existing.inventory.itemInMainHand)
	}

	@Test
	fun testUpdateSimple() {
		val created = repository.create(simpleEntity(4))

		created.inventory.itemInMainHand = ItemStack(Material.DIRT)
		repository.update(created)

		val afterUpdate = repository.forId(created.id)?.inventory?.itemInMainHand

		assertEquals(created.inventory.itemInMainHand, afterUpdate)
	}
}
