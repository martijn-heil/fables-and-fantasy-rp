package com.fablesfantasyrp.plugin.profile

import com.fablesfantasyrp.plugin.profile.data.entity.Profile
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import org.bukkit.Server
import org.bukkit.entity.Player
import org.bukkit.plugin.PluginManager
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertNull

internal class ProfileManagerTest {
	private val profile1 = Profile(
		id = 1,
		owner = null,
		description = null,
		isActive = true
	)

	private val profile2 = Profile(
		id = 2,
		owner = null,
		description = null,
		isActive = true
	)

	private val player1 = mockk<Player>()
	private val player2 = mockk<Player>()

	private val server = mockk<Server>()
	private val pluginManager = mockk<PluginManager>()

	init {
		every { pluginManager.callEvent(any()) } just Runs
		every { server.pluginManager } returns pluginManager
		every { player1.uniqueId } returns UUID.randomUUID()
		every { player1.name } returns "player1"
		every { player2.uniqueId } returns UUID.randomUUID()
		every { player2.name } returns "player2"
		every { server.getPlayer(player1.uniqueId) } returns player1
		every { server.getPlayer(player2.uniqueId) } returns player2
	}

	@Test
	fun testInitialState() {
		val profileManager = ProfileManagerImpl(server)

		assertNull(profileManager.getCurrentForProfile(profile1))
		assertNull(profileManager.getCurrentForProfile(profile2))
	}

	@Test
	fun testSetCurrentForPlayer() {
		val profileManager = ProfileManagerImpl(server)

		profileManager.setCurrentForPlayer(player1, profile1)
		profileManager.setCurrentForPlayer(player2, profile2)

		assertEquals(profileManager.getCurrentForProfile(profile1), player1)
		assertEquals(profileManager.getCurrentForProfile(profile2), player2)

		profileManager.stopTracking(player1)

		assertNull(profileManager.getCurrentForPlayer(player1))
	}

	@Test
	fun testProfileOccupied() {
		val profileManager = ProfileManagerImpl(server)

		profileManager.setCurrentForPlayer(player1, profile1)

		assertThrows<ProfileOccupiedException> { profileManager.setCurrentForPlayer(player2, profile1) }
		assertDoesNotThrow { profileManager.setCurrentForPlayer(player2, profile1, true) }
	}
}
