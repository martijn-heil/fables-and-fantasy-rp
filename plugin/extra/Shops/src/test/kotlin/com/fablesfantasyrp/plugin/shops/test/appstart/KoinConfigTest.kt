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
package com.fablesfantasyrp.plugin.shops.test.appstart

import com.fablesfantasyrp.plugin.domain.premium.PremiumRankCalculator
import com.fablesfantasyrp.plugin.economy.data.entity.ProfileEconomyRepository
import com.fablesfantasyrp.plugin.profile.ProfileManager
import com.fablesfantasyrp.plugin.profile.command.provider.ProfileModule
import com.fablesfantasyrp.plugin.profile.data.entity.EntityProfileRepository
import com.fablesfantasyrp.plugin.profile.data.entity.ProfileRepository
import com.fablesfantasyrp.plugin.shops.appstart.KoinConfig
import com.sk89q.worldguard.protection.regions.RegionContainer
import io.mockk.every
import io.mockk.mockk
import org.bukkit.Server
import org.bukkit.plugin.java.JavaPlugin
import org.junit.jupiter.api.Test
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.test.check.checkModules
import javax.sql.DataSource

internal class KoinConfigTest {
	@Test
	fun testKoinConfig() {
		val plugin = mockk<JavaPlugin>()
		val server = mockk<Server>()
		val profileManager = mockk<ProfileManager>()
		val profiles = mockk<EntityProfileRepository>()
		val dataSource = mockk<DataSource>()
		val profileModule = mockk<ProfileModule>()
		val premiumRankCalculator = mockk<PremiumRankCalculator>()
		val regionContainer = mockk<RegionContainer>()
		val profileEconomyRepository = mockk<ProfileEconomyRepository>()

		every { plugin.server } returns server

		stopKoin()
		val container = startKoin {
			modules(module {
				single { server }
				single { dataSource }
				single { profiles }
				single { profileModule }
				single<ProfileRepository> { profiles }
				single { profileManager }
				single { profileEconomyRepository }
				single { premiumRankCalculator }
				single { regionContainer }
			}, KoinConfig(plugin).koinModule)
		}
		container.checkModules()
		container.close()
		stopKoin()
	}
}
