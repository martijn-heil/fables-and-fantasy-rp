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
package com.fablesfantasyrp.plugin.characters.test

import com.fablesfantasyrp.plugin.characters.dal.enums.Gender
import com.fablesfantasyrp.plugin.characters.dal.enums.Race
import com.fablesfantasyrp.plugin.characters.domain.CharacterStats
import com.fablesfantasyrp.plugin.characters.domain.entity.Character
import com.fablesfantasyrp.plugin.characters.flaunch
import com.fablesfantasyrp.plugin.inventory.PassthroughInventory
import com.fablesfantasyrp.plugin.inventory.PassthroughPlayerInventory
import com.fablesfantasyrp.plugin.inventory.domain.entity.ProfileInventory
import com.fablesfantasyrp.plugin.inventory.domain.repository.ProfileInventoryRepository
import com.fablesfantasyrp.plugin.profile.ProfileManager
import com.fablesfantasyrp.plugin.profile.data.entity.Profile
import com.fablesfantasyrp.plugin.time.FablesInstantSource
import com.fablesfantasyrp.plugin.time.javatime.FablesLocalDate
import com.fablesfantasyrp.plugin.time.javatime.FablesLocalDateTime
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.runBlocking
import org.bukkit.Bukkit
import org.bukkit.plugin.SimpleServicesManager
import org.junit.jupiter.api.Test
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import java.time.Instant
import kotlin.test.assertFalse
import kotlin.test.assertTrue

internal class CharacterTest {

	init {
		val stubInstantSource = object : FablesInstantSource {
			override fun instant(): Instant
				= Instant.ofEpochSecond(FablesLocalDateTime.of(1550, 2, 2, 1, 1, 1).toEpochSecond())
		}

		mockkStatic(::flaunch)

		val profileManager = mockk<ProfileManager>()
		every { profileManager.getCurrentForProfile(any()) } returns null
		every { flaunch(any()) } answers { runBlocking { firstArg<suspend CoroutineScope.() -> Unit>()(); mockk() } }

		val inventoryRepository = mockk<ProfileInventoryRepository>()
		coEvery { inventoryRepository.forOwner(any()) } returns ProfileInventory(0,
			PassthroughPlayerInventory.createEmpty(),
			PassthroughInventory(emptyArray()))

		val servicesManager = SimpleServicesManager()
		mockkStatic(Bukkit::class)
		every { Bukkit.getServicesManager() } returns servicesManager

		stopKoin()
		startKoin {
			modules(module {
				single<ProfileManager> { profileManager }
				single<FablesInstantSource> { stubInstantSource }
				single<ProfileInventoryRepository> { inventoryRepository }
			})
		}
	}

	@Test
	fun testIsDead() {
		val character1 = makeCharacter(FablesLocalDate.of(1550, 2, 1))
		val character2 = makeCharacter(FablesLocalDate.of(1550, 2, 2))
		val character3 = makeCharacter(FablesLocalDate.of(1550, 2, 3))

		val character4 = makeCharacter(FablesLocalDate.of(1549, 2, 1))
		val character5 = makeCharacter(FablesLocalDate.of(1549, 2, 2))
		val character6 = makeCharacter(FablesLocalDate.of(1549, 2, 3))

		val character7 = makeCharacter(FablesLocalDate.of(1551, 2, 1))
		val character8 = makeCharacter(FablesLocalDate.of(1551, 2, 2))
		val character9 = makeCharacter(FablesLocalDate.of(1551, 2, 3))

		val character10 = makeCharacter(FablesLocalDate.of(1550, 1, 1))
		val character11 = makeCharacter(FablesLocalDate.of(1550, 1, 2))
		val character12 = makeCharacter(FablesLocalDate.of(1550, 1, 3))

		val character13 = makeCharacter(FablesLocalDate.of(1550, 3, 1))
		val character14 = makeCharacter(FablesLocalDate.of(1550, 3, 2))
		val character15 = makeCharacter(FablesLocalDate.of(1550, 3, 3))

		assertTrue { character1.isDead }
		assertTrue { character2.isDead }
		assertFalse { character3.isDead }

		assertTrue { character4.isDead }
		assertTrue { character5.isDead }
		assertTrue { character6.isDead }

		assertFalse { character7.isDead }
		assertFalse { character8.isDead }
		assertFalse { character9.isDead }

		assertTrue { character10.isDead }
		assertTrue { character11.isDead }
		assertTrue { character12.isDead }

		assertFalse { character13.isDead }
		assertFalse { character14.isDead }
		assertFalse { character15.isDead }
	}

	@Test
	fun testIsDying() {
		val character1 = makeCharacter(FablesLocalDate.of(1550, 8, 1))
		val character2 = makeCharacter(FablesLocalDate.of(1550, 8, 2))
		val character3 = makeCharacter(FablesLocalDate.of(1550, 8, 3))

		assertTrue { character1.isDying }
		assertFalse { character2.isDying }
		assertFalse { character3.isDying }
	}

	private fun makeCharacter(dateOfNaturalDeath: FablesLocalDate)
		= Character(
		id = 0,
		profile = Profile(id = 0, null, null, true),
		name = "Character 1",
		description = "Character 1 description",
		stats = CharacterStats(),
		race = Race.ATTIAN_HUMAN,
		gender = Gender.MALE,
		dateOfBirth = FablesLocalDate.of(1450, 1, 1),
		dateOfNaturalDeath = dateOfNaturalDeath,
		lastSeen = null,
		createdAt = null,
		diedAt = null,
		shelvedAt = null,
		changedStatsAt = null,
	)
}
