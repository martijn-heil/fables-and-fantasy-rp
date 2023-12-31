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
