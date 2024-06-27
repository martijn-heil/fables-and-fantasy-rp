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
package com.fablesfantasyrp.plugin.database

import com.fablesfantasyrp.plugin.utils.enforceDependencies
import org.bukkit.plugin.Plugin
import org.bukkit.plugin.java.JavaPlugin
import org.flywaydb.core.Flyway
import org.flywaydb.core.api.FlywayException
import org.h2.jdbcx.JdbcConnectionPool
import org.koin.core.component.KoinComponent
import org.koin.core.context.loadKoinModules
import org.koin.core.context.unloadKoinModules
import org.koin.core.module.Module
import org.koin.dsl.module
import javax.sql.DataSource

class FablesDatabase : JavaPlugin(), KoinComponent {
	private lateinit var koinModule: Module

	override fun onEnable() {
		enforceDependencies(this)
		instance = this

		logger.fine("Saving default config..")
		saveDefaultConfig()

		logger.info("Migrating database if needed..")
		dbUrl = config.getString("db.url")!!
		dbUsername = config.getString("db.username")!!
		dbPassword = config.getString("db.password")!!
		// Storing the password in a char array doesn't improve much..
		// it's stored in plaintext in the "config" object anyway.. :/

		val dataSource = JdbcConnectionPool.create(dbUrl, dbUsername, dbPassword)
		dataSource.maxConnections = 50
		dataSource.loginTimeout = 5

		printDatabaseSettings(dataSource)

		fablesDatabase = dataSource

		try {
			applyMigrations(this, null, this.classLoader)
		} catch (ex: Exception) {
			this.isEnabled = false
			return
		}

		koinModule = module {
			single<DataSource> { dataSource }
		}
		loadKoinModules(koinModule)
	}

	override fun onDisable() {
		unloadKoinModules(koinModule)
	}

	private fun printDatabaseSettings(dataSource: DataSource) {
		val lines = dataSource.connection.use { connection ->
			val stmnt = connection.prepareStatement("SELECT * FROM INFORMATION_SCHEMA.SETTINGS")
			val result = stmnt.executeQuery()
			val lines = ArrayList<String>()
			while (result.next()) {
				lines.add("${result.getString("SETTING_NAME")}: ${result.getString("SETTING_VALUE")}")
			}
			lines
		}
		logger.info("H2 Database settings:\n" + lines.joinToString("\n"))
	}

	companion object {
		lateinit var instance: FablesDatabase
		private lateinit var dbUrl: String
		private lateinit var dbUsername: String
		private lateinit var dbPassword: String

		lateinit var fablesDatabase: DataSource
			private set
	}
}

fun applyMigrations(plugin: Plugin, schema: String?, classLoader: ClassLoader) {
	val flywayConfig = Flyway.configure(classLoader)
			.dataSource(FablesDatabase.fablesDatabase)
			.locations(
					"db/migration",
					"${plugin.javaClass.packageName}.db.migration".replace('.', '/'))
			.defaultSchema(schema)
			.baselineOnMigrate(true)
			.baselineVersion("0")

	if (schema != null) flywayConfig.defaultSchema(schema)

	val flyway = flywayConfig.load()

	try {
		plugin.logger.info("Successfully applied ${flyway.migrate()} migrations")
	} catch (ex: FlywayException) {
		plugin.logger.severe(ex.message)
		ex.printStackTrace()
		try {
			flyway.repair()
		} catch (ex: FlywayException) {
			plugin.logger.severe("Error whilst attempting to repair migrations:")
			ex.printStackTrace()
			throw ex
		}
	}
}
