package com.fablesfantasyrp.plugin.database.testfixtures

import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import org.bukkit.Server
import org.bukkit.plugin.Plugin
import java.util.logging.Logger

abstract class BaseH2RepositoryTest(defaultSchema: String) {
	protected val dataSource = createTestDataSource(defaultSchema)
	protected val plugin: Plugin = mockk<Plugin>()

	init {
		val logger = mockk<Logger>()
		val server = mockk<Server>()

		every { server.isPrimaryThread } returns false
		every { server.currentTick } returns 1
		every { plugin.server } returns server
		every { plugin.logger } returns logger
		every { logger.warning(any<String>()) } just runs
	}
}
