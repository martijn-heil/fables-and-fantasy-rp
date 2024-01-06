package com.fablesfantasyrp.plugin.inventory.test

import com.fablesfantasyrp.plugin.database.CacheMarker
import com.fablesfantasyrp.plugin.database.repository.DirtyMarker
import com.fablesfantasyrp.plugin.inventory.PassthroughPlayerInventory
import com.fablesfantasyrp.plugin.inventory.SerializableInventory
import com.fablesfantasyrp.plugin.utils.extensions.bukkit.PLAYER_HOTBAR_SLOTS
import io.mockk.*
import org.bukkit.inventory.PlayerInventory
import org.junit.jupiter.api.Test

internal class PassthroughPlayerInventoryTest {
	@Test
	fun `setting bukkit inventory marks strong`() {
		val cacheMarker = mockk<CacheMarker<SerializableInventory>>()
		val playerInventory = mockk<PlayerInventory>()

		every { cacheMarker.markStrong(any()) } just runs
		every { playerInventory.contents } returns arrayOfNulls(PassthroughPlayerInventory.size)
		every { playerInventory.contents = any() } just runs
		every { playerInventory.heldItemSlot = any() } just runs

		val inventory = PassthroughPlayerInventory.createEmpty()
		inventory.cacheMarker = cacheMarker

		inventory.bukkitInventory = playerInventory

		verify { cacheMarker.markStrong(inventory) }
	}

	@Test
	fun `unsetting bukkit inventory marks weak`() {
		val cacheMarker = mockk<CacheMarker<SerializableInventory>>()
		val playerInventory = mockk<PlayerInventory>()

		every { cacheMarker.markWeak(any()) } just runs
		every { playerInventory.contents } returns arrayOfNulls(PassthroughPlayerInventory.size)
		every { playerInventory.contents = any() } just runs
		every { playerInventory.heldItemSlot = any() } just runs
		every { playerInventory.heldItemSlot } returns PLAYER_HOTBAR_SLOTS.first

		val inventory = PassthroughPlayerInventory.createEmpty()

		inventory.bukkitInventory = playerInventory

		inventory.cacheMarker = cacheMarker

		inventory.bukkitInventory = null

		verify { cacheMarker.markWeak(inventory) }
	}

	@Test
	fun `unsetting bukkit inventory marks dirty`() {
		val dirtyMarker = mockk<DirtyMarker<SerializableInventory>>()
		val playerInventory = mockk<PlayerInventory>()

		every { dirtyMarker.markDirty(any()) } just runs
		every { playerInventory.contents } returns arrayOfNulls(PassthroughPlayerInventory.size)
		every { playerInventory.contents = any() } just runs
		every { playerInventory.heldItemSlot = any() } just runs
		every { playerInventory.heldItemSlot } returns PLAYER_HOTBAR_SLOTS.first

		val inventory = PassthroughPlayerInventory.createEmpty()
		inventory.bukkitInventory = playerInventory

		inventory.dirtyMarker = dirtyMarker

		inventory.bukkitInventory = null

		verify { dirtyMarker.markDirty(any()) }
	}
}
