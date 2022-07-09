package com.fablesfantasyrp.plugin.database

import org.bukkit.plugin.java.JavaPlugin
import org.flywaydb.core.Flyway
import org.flywaydb.core.api.FlywayException
import org.flywaydb.core.api.MigrationVersion
import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException

class FablesDatabase : JavaPlugin() {
	override fun onEnable() {
		logger.fine("Saving default config..")
		saveDefaultConfig()

		logger.info("Migrating database if needed..")
		dbUrl = config.getString("db.url")!!
		dbUsername = config.getString("db.username")!!
		dbPassword = config.getString("db.password")!!
		// Storing the password in a char array doesn't improve much..
		// it's stored in plaintext in the "config" object anyway.. :/

		// For some reason Flyway just can't reliably find the migrations as a resource in the jar
		// So instead we just put them on the filesystem and let flyway find them there
		this.saveResource("db/migration/V1__create_tables.sql", true)
		this.saveResource("db/migration/V2__chat.sql", true)

		server.scheduler.scheduleSyncDelayedTask(this, {
			val flyway = Flyway(classLoader)
			flyway.setLocations("filesystem:${this.dataFolder.resolve("db/migration").path}")
			flyway.setDataSource(dbUrl, dbUsername, dbPassword)
			flyway.isBaselineOnMigrate = true;
			flyway.baselineVersion = MigrationVersion.fromVersion("0");

			try {
				logger.info("Successfully applied ${flyway.migrate()} migrations")
			} catch (ex: FlywayException) {
				logger.severe(ex.message)
				ex.printStackTrace()
				try {
					flyway.repair()
				} catch (ex: FlywayException) {
					logger.severe("Error whilst attempting to repair migrations:")
					ex.printStackTrace()
					logger.severe("Continuing to disable plugin..")
					this.isEnabled = false
					return@scheduleSyncDelayedTask
				}
			}

			try {
				dbconn = DriverManager.getConnection(dbUrl, dbUsername, dbPassword)
			} catch (ex: SQLException) {
				logger.severe(ex.message)
				logger.severe("Disabling plugin due to database error..")
				this.isEnabled = false
			}
		}, 0)
	}

	override fun onDisable() {
		dbconn!!.close()
	}

	companion object {
		lateinit var instance: FablesDatabase
		private lateinit var dbUrl: String
		private lateinit var dbUsername: String
		private lateinit var dbPassword: String

		val fablesDatabase: Connection
			get() = dbconn!!

		private var dbconn: Connection? = null
			get() {
				if(field!!.isClosed) field = DriverManager.getConnection(dbUrl, dbUsername, dbPassword)

				return field
			}
	}
}
