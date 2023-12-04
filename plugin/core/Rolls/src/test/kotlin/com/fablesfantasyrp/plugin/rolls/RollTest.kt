package com.fablesfantasyrp.plugin.rolls

import com.fablesfantasyrp.plugin.characters.dal.enums.CharacterStatKind
import com.fablesfantasyrp.plugin.characters.domain.CharacterStats
import com.fablesfantasyrp.plugin.characters.domain.entity.Character
import com.fablesfantasyrp.plugin.location.data.entity.ProfileLocation
import com.fablesfantasyrp.plugin.location.data.entity.ProfileLocationRepository
import com.fablesfantasyrp.plugin.profile.data.entity.Profile
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.plugin.SimpleServicesManager
import org.junit.jupiter.api.Test
import org.koin.core.context.startKoin
import org.koin.dsl.module
import kotlin.random.Random
import kotlin.test.assertEquals

internal class RollTest {
	private val highStats = CharacterStats(
		strength = 12U,
		intelligence = 10U,
		defense = 8U,
		agility = 6U
	)

	private val abnormallyHighStats = CharacterStats(
		strength = 20U,
		intelligence = 18U,
		defense = 16U,
		agility = 14U
	)

	private val lowStats = CharacterStats(
		strength = 2U,
		intelligence = 4U,
		defense = 6U,
		agility = 8U
	)

	private val oddStats = CharacterStats(
		strength = 3U,
		intelligence = 5U,
		defense = 7U,
		agility = 9U
	)

	private val always1 = mockk<Random>()

	init {
		every { always1.nextInt(any(), any()) } returns 1

		val world = mockk<World>()
		every { world.isDayTime } returns true

		val servicesManager = SimpleServicesManager()
		mockkStatic(Bukkit::class)
		every { Bukkit.getServicesManager() } returns servicesManager

		val profileLocationRepositoryMock = mockk<ProfileLocationRepository>()
		val location = Location(world, 0.00, 0.00, 0.00)
		every { profileLocationRepositoryMock.forOwner(any()) } returns ProfileLocation(0, location)

		startKoin {
			modules(module {
				single { profileLocationRepositoryMock }
			})
		}
	}

	@Test
	fun testStatRolls() {
		val highStatsCharacter = mockCharacter(highStats)
		assertEquals(roll(always1, 20U, highStatsCharacter, CharacterStatKind.STRENGTH), Pair(1U, 4))
		assertEquals(roll(always1, 20U, highStatsCharacter, CharacterStatKind.INTELLIGENCE), Pair(1U, 3))
		assertEquals(roll(always1, 20U, highStatsCharacter, CharacterStatKind.DEFENSE), Pair(1U, 2))
		assertEquals(roll(always1, 20U, highStatsCharacter, CharacterStatKind.AGILITY), Pair(1U, 1))

		val lowStatsCharacter = mockCharacter(lowStats)
		assertEquals(roll(always1, 20U, lowStatsCharacter, CharacterStatKind.STRENGTH), Pair(1U, -1))
		assertEquals(roll(always1, 20U, lowStatsCharacter, CharacterStatKind.INTELLIGENCE), Pair(1U, 0))
		assertEquals(roll(always1, 20U, lowStatsCharacter, CharacterStatKind.DEFENSE), Pair(1U, 1))
		assertEquals(roll(always1, 20U, lowStatsCharacter, CharacterStatKind.AGILITY), Pair(1U, 2))

		val oddStatsCharacter = mockCharacter(oddStats)
		assertEquals(roll(always1, 20U, oddStatsCharacter, CharacterStatKind.STRENGTH), Pair(1U, -1))
		assertEquals(roll(always1, 20U, oddStatsCharacter, CharacterStatKind.INTELLIGENCE), Pair(1U, 0))
		assertEquals(roll(always1, 20U, oddStatsCharacter, CharacterStatKind.DEFENSE), Pair(1U, 1))
		assertEquals(roll(always1, 20U, oddStatsCharacter, CharacterStatKind.AGILITY), Pair(1U, 2))

		val abnormallyHighStatsCharacter = mockCharacter(abnormallyHighStats)
		assertEquals(roll(always1, 20U, abnormallyHighStatsCharacter, CharacterStatKind.STRENGTH), Pair(1U, 8))
		assertEquals(roll(always1, 20U, abnormallyHighStatsCharacter, CharacterStatKind.INTELLIGENCE), Pair(1U, 7))
		assertEquals(roll(always1, 20U, abnormallyHighStatsCharacter, CharacterStatKind.DEFENSE), Pair(1U, 6))
		assertEquals(roll(always1, 20U, abnormallyHighStatsCharacter, CharacterStatKind.AGILITY), Pair(1U, 5))
	}

	private fun mockCharacter(stats: CharacterStats): Character {
		val profile = mockk<Profile>()
		val character = mockk<Character>()
		every { character.totalStats } returns stats
		every { character.profile } returns profile

		return character
	}
}
