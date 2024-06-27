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

import org.flywaydb.core.Flyway
import org.h2.jdbcx.JdbcConnectionPool
import javax.sql.DataSource

fun createTestDataSource(defaultSchema: String): DataSource {
	val dataSource = JdbcConnectionPool.create("jdbc:h2:mem:test", "test", "").apply {
		maxConnections = 50
		loginTimeout = 5
	}

	val flywayConfig = Flyway.configure()
		.dataSource(dataSource)
		.locations("filesystem:src/main/resources/db/migration")
		.defaultSchema(defaultSchema)
		.baselineOnMigrate(true)
		.baselineVersion("0")

	val flyway = flywayConfig.load()
	flyway.migrate()

	return dataSource
}
