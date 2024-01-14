package com.fablesfantasyrp.plugin.activity.test.appstart

import com.fablesfantasyrp.plugin.activity.appstart.KoinConfig
import com.sk89q.worldguard.protection.regions.RegionContainer
import io.mockk.every
import io.mockk.mockk
import org.bukkit.Server
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.plugin.java.JavaPlugin
import org.junit.jupiter.api.Test
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.test.check.checkModules

internal class KoinConfigTest {
	@Test
	fun testKoinConfig() {
		val plugin = mockk<JavaPlugin>()
		val server = mockk<Server>()
		val regionContainer = mockk<RegionContainer>()
		val fileConfiguration = mockk<FileConfiguration>()

		every { plugin.server } returns server
		every { plugin.config } returns fileConfiguration

		stopKoin()
		val container = startKoin {
			modules(module {
				single { server }
				single { regionContainer }
			}, KoinConfig(plugin).koinModule)
		}
		container.checkModules()
		container.close()
		stopKoin()
	}
}
