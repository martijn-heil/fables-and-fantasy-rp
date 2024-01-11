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
