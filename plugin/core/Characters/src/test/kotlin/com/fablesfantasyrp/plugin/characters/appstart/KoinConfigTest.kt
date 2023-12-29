package com.fablesfantasyrp.plugin.characters.appstart

import com.fablesfantasyrp.caturix.parametric.Provider
import com.fablesfantasyrp.caturix.spigot.common.bukkit.provider.OfflinePlayerProvider
import com.fablesfantasyrp.caturix.spigot.common.bukkit.provider.PlayerProvider
import com.fablesfantasyrp.plugin.profile.ProfileManager
import com.fablesfantasyrp.plugin.profile.ProfilePrompter
import com.fablesfantasyrp.plugin.profile.command.provider.ProfileProvider
import com.fablesfantasyrp.plugin.profile.data.entity.EntityProfileRepository
import com.fablesfantasyrp.plugin.profile.data.entity.ProfileRepository
import com.fablesfantasyrp.plugin.staffprofiles.data.StaffProfileRepository
import io.mockk.every
import io.mockk.mockk
import org.bukkit.Server
import org.bukkit.plugin.java.JavaPlugin
import org.junit.jupiter.api.Test
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.core.module.dsl.named
import org.koin.core.module.dsl.withOptions
import org.koin.dsl.bind
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
		val profilePrompter = mockk<ProfilePrompter>()
		val staffProfileRepository = mockk<StaffProfileRepository>()
		val dataSource = mockk<DataSource>()

		every { plugin.server } returns server

		stopKoin()
		val container = startKoin {
			modules(module {
				single { server }
				single { dataSource }
				single { profiles }
				single<ProfileRepository> { profiles }
				single { profileManager }
				single { profilePrompter }
				single { staffProfileRepository }
				factory {
					ProfileProvider(
						profiles,
						profileManager,
						PlayerProvider(server, OfflinePlayerProvider(server)),
						server
					)
				} bind Provider::class withOptions { named("Profile") }
			}, KoinConfig(plugin).koinModule)
		}
		container.checkModules()
		container.close()
		stopKoin()
	}
}
