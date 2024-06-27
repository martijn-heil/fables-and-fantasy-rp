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
